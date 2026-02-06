//! Path runner trait and status. Platform apps implement PathRunner for each protocol.

use crate::config::PathConfig;
use async_trait::async_trait;

#[derive(Clone, Debug)]
pub enum PathStatus {
    Connected,
    Disconnected,
    Failed(String),
}

#[async_trait]
pub trait PathRunner: Send + Sync {
    async fn connect(&self, config: PathConfig) -> Result<(), Box<dyn std::error::Error + Send + Sync>>;
    async fn status(&self, config: &PathConfig) -> PathStatus;
    async fn disconnect(&self) -> Result<(), Box<dyn std::error::Error + Send + Sync>>;
}
