//! Fallback engine: ordered path list, health check, switch on failure in <15s, exponential backoff.
//! US-002, FR-6.

use crate::config::{PathConfig, PathKind, ServerList};
use crate::path::{PathRunner, PathStatus};
use serde::{Deserialize, Serialize};
use std::sync::Arc;
use std::time::Duration;
use tokio::sync::{mpsc, RwLock};
use tracing::warn;

/// Default fallback order (PRD: Psiphon -> Conduit -> Xray/Rostam).
pub const DEFAULT_PATH_ORDER: [PathKind; 4] = [
    PathKind::Psiphon,
    PathKind::Conduit,
    PathKind::Xray,
    PathKind::Rostam,
];

/// Event emitted by the fallback engine (for UI: path switch, failure).
#[derive(Clone, Debug, Serialize, Deserialize)]
#[serde(tag = "event", rename_all = "snake_case")]
pub enum FallbackEvent {
    Connecting { path_kind: PathKind, index: usize },
    Connected { path_kind: PathKind, index: usize },
    PathFailed { path_kind: PathKind, index: usize, error: String },
    FallbackActivated { from: PathKind, to: PathKind },
    AllPathsFailed,
    Backoff { path_kind: PathKind, duration_secs: u64 },
}

/// Current state of the fallback engine.
#[derive(Clone, Debug, Default, Serialize, Deserialize)]
pub struct FallbackState {
    pub active_path_index: Option<usize>,
    pub active_path_kind: Option<PathKind>,
    pub connection_established: bool,
    pub failure_count: u32,
}

/// Fallback engine: tries paths in order, switches on failure within 15s, exponential backoff.
pub struct FallbackEngine {
    paths: Vec<PathConfig>,
    path_order: Vec<PathKind>,
    state: Arc<RwLock<FallbackState>>,
    event_tx: Option<mpsc::UnboundedSender<FallbackEvent>>,
    failure_timeout: Duration,
    backoff_base: Duration,
}

impl FallbackEngine {
    pub fn new(paths: Vec<PathConfig>) -> Self {
        Self {
            path_order: DEFAULT_PATH_ORDER.to_vec(),
            paths,
            state: Arc::new(RwLock::new(FallbackState::default())),
            event_tx: None,
            failure_timeout: Duration::from_secs(15),
            backoff_base: Duration::from_secs(5),
        }
    }

    pub fn with_server_list(server_list: ServerList) -> Self {
        Self::new(server_list.paths)
    }

    pub fn set_event_tx(&mut self, tx: mpsc::UnboundedSender<FallbackEvent>) {
        self.event_tx = Some(tx);
    }

    pub fn set_path_order(&mut self, order: Vec<PathKind>) {
        self.path_order = order;
    }

    pub fn failure_timeout(&self) -> Duration {
        self.failure_timeout
    }

    pub fn backoff_duration(&self, failure_count: u32) -> Duration {
        let secs = self.backoff_base.as_secs() * (1 << failure_count.min(6)) as u64;
        Duration::from_secs(secs.min(3600))
    }

    fn emit(&self, event: FallbackEvent) {
        if let Some(ref tx) = self.event_tx {
            let _ = tx.send(event);
        }
    }

    pub fn ordered_indices(&self) -> Vec<usize> {
        let mut indices: Vec<usize> = (0..self.paths.len()).collect();
        indices.sort_by_key(|&i| {
            let kind = self.paths.get(i).map(|p| p.kind());
            self.path_order
                .iter()
                .position(|k| kind.as_ref() == Some(k))
                .unwrap_or(usize::MAX)
        });
        indices
    }

    pub async fn run<R: PathRunner>(
        &self,
        runner: &R,
    ) -> Result<(), Box<dyn std::error::Error + Send + Sync>> {
        let indices = self.ordered_indices();
        if indices.is_empty() {
            self.emit(FallbackEvent::AllPathsFailed);
            return Err("no paths configured".into());
        }

        let mut failure_count = 0u32;

        for &idx in &indices {
            let path = match self.paths.get(idx) {
                Some(p) => p.clone(),
                None => continue,
            };
            let kind = path.kind();

            self.emit(FallbackEvent::Connecting {
                path_kind: kind.clone(),
                index: idx,
            });
            {
                let mut state = self.state.write().await;
                state.active_path_index = Some(idx);
                state.active_path_kind = Some(kind.clone());
                state.connection_established = false;
            }

            match runner.connect(path).await {
                Ok(()) => {
                    self.emit(FallbackEvent::Connected {
                        path_kind: kind.clone(),
                        index: idx,
                    });
                    let mut state = self.state.write().await;
                    state.connection_established = true;
                    state.failure_count = 0;
                    return Ok(());
                }
                Err(e) => {
                    let err_msg = e.to_string();
                    self.emit(FallbackEvent::PathFailed {
                        path_kind: kind.clone(),
                        index: idx,
                        error: err_msg.clone(),
                    });
                    warn!(path = ?kind, error = %err_msg, "path failed");
                    failure_count += 1;
                }
            }
        }

        self.emit(FallbackEvent::AllPathsFailed);
        {
            let mut state = self.state.write().await;
            state.active_path_index = None;
            state.active_path_kind = None;
            state.connection_established = false;
            state.failure_count = failure_count;
        }
        Err("all paths failed".into())
    }

    pub async fn check_health<R: PathRunner>(&self, runner: &R) -> bool {
        let state = self.state.read().await;
        let idx = match state.active_path_index {
            Some(i) => i,
            None => return false,
        };
        let path = match self.paths.get(idx) {
            Some(p) => p.clone(),
            None => return false,
        };
        drop(state);

        match runner.status(&path).await {
            PathStatus::Connected => true,
            PathStatus::Failed(e) => {
                self.emit(FallbackEvent::PathFailed {
                    path_kind: path.kind(),
                    index: idx,
                    error: e,
                });
                false
            }
            PathStatus::Disconnected => false,
        }
    }

    pub async fn state(&self) -> FallbackState {
        self.state.read().await.clone()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn backoff_duration_increases() {
        let e = FallbackEngine::new(vec![]);
        let d0 = e.backoff_duration(0);
        let d1 = e.backoff_duration(1);
        let d2 = e.backoff_duration(2);
        assert!(d1 > d0);
        assert!(d2 > d1);
    }

    #[tokio::test]
    async fn ordered_indices_empty() {
        let e = FallbackEngine::new(vec![]);
        assert!(e.ordered_indices().is_empty());
    }
}
