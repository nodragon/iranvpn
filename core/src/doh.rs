//! DNS-over-HTTPS (DoH) client stub. US-006, FR-9.
//! Full implementation: use when DoH is available; fallback to tunneled DNS when blocked.

use anyhow::Result;
use serde::{Deserialize, Serialize};
use std::net::IpAddr;
use std::time::Duration;

/// DoH client (stub). Resolve hostname via DoH; graceful no-op or tunnel fallback when blocked.
#[derive(Clone, Debug)]
pub struct DohClient {
    pub provider_url: String,
    pub timeout: Duration,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct DohResponse {
    #[serde(default)]
    pub Status: i32,
    #[serde(default)]
    pub Answer: Vec<DohAnswer>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct DohAnswer {
    #[serde(rename = "type")]
    pub typ: u16,
    pub data: String,
}

impl Default for DohClient {
    fn default() -> Self {
        Self {
            provider_url: "https://cloudflare-dns.com/dns-query".to_string(),
            timeout: Duration::from_secs(5),
        }
    }
}

impl DohClient {
    pub fn new(provider_url: String) -> Self {
        Self {
            provider_url,
            timeout: Duration::from_secs(5),
        }
    }

    /// Resolve a hostname to IPs. Stub: returns empty vec; full impl would perform DoH query.
    pub async fn resolve(&self, _name: &str) -> Result<Vec<IpAddr>> {
        // Stub: no actual DoH request yet. Real impl would:
        // - GET/POST to provider_url with ?name=...&type=A (and AAAA)
        // - Parse JSON (Google/Cloudflare format) or DNS wire format
        Ok(Vec::new())
    }

    /// Check if DoH is reachable (for graceful fallback when DoT/DoH blocked).
    pub async fn is_available(&self) -> bool {
        let client = reqwest::Client::builder()
            .timeout(self.timeout)
            .build()
            .unwrap_or_default();
        client
            .get(&self.provider_url)
            .query(&[("name", "example.com"), ("type", "A")])
            .header("Accept", "application/dns-json")
            .send()
            .await
            .map(|r| r.status().is_success())
            .unwrap_or(false)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[tokio::test]
    async fn doh_resolve_stub_returns_empty() {
        let c = DohClient::default();
        let ips = c.resolve("example.com").await.unwrap();
        assert!(ips.is_empty());
    }
}
