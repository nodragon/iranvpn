//! Config distribution: fetch server list from 2+ sources (FR-8). Zero-config.

use crate::config::{ConfigSource, ServerList};
use anyhow::Result;
use reqwest::Client;
use std::time::Duration;
use tracing::info;

const FETCH_TIMEOUT: Duration = Duration::from_secs(15);

/// Default config sources for zero-config. FR-8: at least 2 redundant sources.
/// Replace URLs with real primary + mirror when dist-config is deployed.
pub fn default_config_sources() -> Vec<ConfigSource> {
    vec![
        ConfigSource {
            url: "https://raw.githubusercontent.com/opensignalfoundation/iran-vpn/main/dist-config/server-list.json"
                .to_string(),
            label: Some("Primary (GitHub)".to_string()),
        },
        ConfigSource {
            url: "https://primary.example.com/config.json".to_string(),
            label: Some("Mirror".to_string()),
        },
    ]
}

/// Fallback server list when all config sources fail (embedded bootstrap).
pub fn fallback_server_list() -> ServerList {
    serde_json::from_str(r#"{"paths":[{"type":"psiphon","server_list_url":null},{"type":"conduit","discovery_url":"https://conduit.psiphon.ca/discovery"}],"sources":[]}"#)
        .unwrap_or_default()
}

/// Fetch server list from first successful of multiple sources (redundant).
pub async fn fetch_server_list(sources: &[ConfigSource]) -> Result<ServerList> {
    let client = Client::builder()
        .timeout(FETCH_TIMEOUT)
        .build()?;

    for source in sources {
        if let Ok(list) = fetch_one(&client, &source.url).await {
            info!(url = %source.url, "fetched server list");
            return Ok(list);
        }
    }
    anyhow::bail!("all config sources failed")
}

async fn fetch_one(client: &Client, url: &str) -> Result<ServerList> {
    let resp = client.get(url).send().await?;
    resp.error_for_status_ref()?;
    let list: ServerList = resp.json().await?;
    Ok(list)
}
