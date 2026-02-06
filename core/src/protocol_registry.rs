//! Protocol registry: pluggable handlers for adding new protocols (US-007, NFR-7).

use crate::config::{PathConfig, PathKind};
use crate::path::PathStatus;
use async_trait::async_trait;
use std::collections::HashMap;
use std::sync::Arc;

/// Protocol handler: knows how to connect and report status for one PathKind.
#[async_trait]
pub trait ProtocolHandler: Send + Sync {
    fn kind(&self) -> PathKind;
    async fn connect(
        &self,
        config: &PathConfig,
    ) -> Result<(), Box<dyn std::error::Error + Send + Sync>>;
    async fn status(&self, config: &PathConfig) -> PathStatus;
    async fn disconnect(&self) -> Result<(), Box<dyn std::error::Error + Send + Sync>>;
}

/// Registry of protocol handlers (pluggable; add new protocols without major refactor).
pub struct ProtocolRegistry {
    handlers: HashMap<PathKind, Arc<dyn ProtocolHandler>>,
}

impl Default for ProtocolRegistry {
    fn default() -> Self {
        Self::new()
    }
}

impl ProtocolRegistry {
    pub fn new() -> Self {
        Self {
            handlers: HashMap::new(),
        }
    }

    pub fn register(&mut self, handler: Arc<dyn ProtocolHandler>) {
        self.handlers.insert(handler.kind(), handler);
    }

    pub fn get(&self, kind: &PathKind) -> Option<Arc<dyn ProtocolHandler>> {
        self.handlers.get(kind).cloned()
    }

    pub fn supported(&self) -> Vec<PathKind> {
        self.handlers.keys().cloned().collect()
    }
}
