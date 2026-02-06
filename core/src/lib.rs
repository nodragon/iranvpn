//! Iran VPN Core — shared library for fallback engine, config, DoH.
//!
//! Used by Android, iOS, Windows, and macOS apps. No UI.

pub mod config;
pub mod doh;
pub mod fetch;
pub mod fallback;
pub mod path;
pub mod protocol_registry;

#[cfg(all(feature = "jni", target_os = "android"))]
pub mod jni_bridge;

pub use config::{ConfigSource, PathConfig, PathKind, ServerList};
pub use fetch::{default_config_sources, fallback_server_list, fetch_server_list};
pub use doh::DohClient;
pub use fallback::{FallbackEngine, FallbackEvent, FallbackState};
pub use path::{PathRunner, PathStatus};
pub use protocol_registry::ProtocolRegistry;
