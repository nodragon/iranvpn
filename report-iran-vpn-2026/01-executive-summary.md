# Executive Summary

## Overview

Iran maintains one of the world's most restrictive internet environments. The government employs a multi-layered censorship apparatus that blocks millions of domains, disrupts non-HTTP(S) traffic, and uses Deep Packet Inspection (DPI) to detect and block VPN and circumvention tools. As of February 2024, unauthorized VPN use is illegal in Iran.

This report documents Iran's filtering infrastructure, explains why standard VPNs fail, and identifies free, open source tools that can operate under these restrictions.

## Key Findings

### Iran's Filtering System

- **Scale:** Over 6 million fully qualified domain names and 3.3 million apex domains blocked
- **Mechanisms:** DNS poisoning, DPI, HTTP blockpage injection, HTTPS/SNI filtering, UDP disruption, and protocol whitelisting (only DNS, HTTP, HTTPS allowed)
- **Blocked content:** Major social media (Facebook, X, Instagram, YouTube, TikTok), messaging apps (Signal, WhatsApp, Telegram), and news outlets (BBC Persian, VOA)

### Why Standard VPNs Fail

- **OpenVPN:** DPI recognizes OpenVPN encapsulation regardless of port (80, 443, 53)
- **Plain WireGuard:** Distinct packet signature; detectable by DPI
- **Cloudflare WARP:** Policy changes have restricted proxy abuse; reliability in Iran is mixed

### What Works

Effective circumvention requires:

1. **Traffic obfuscation** — Tunnel VPN traffic over HTTPS to resemble legitimate web traffic
2. **Protocol-level evasion** — TCP desync, packet manipulation, protocol whitelister bypass
3. **Protocol mimicry** — VMess, VLESS, Trojan over TLS/HTTPS; WebSocket, gRPC, xHTTP, QUIC

## Recommended Open Source Tools

| Tool | Best For | Platform |
|------|----------|----------|
| **Psiphon** | General use; no config needed | Windows, Android, iOS |
| **MahsaNG** | Iran-focused; built-in configs | Android |
| **NikaNG** | Advanced; WARP, WireGuard Noise, Fragment | Android |
| **RostamWG** | Obfuscated WireGuard; DPI bypass | Cross-platform |
| **WARP+Psiphon** | Combined approach | Various |

## Recommendations

1. **Use existing tools** — Deploy Psiphon, MahsaNG, NikaNG, or RostamWG; avoid building from scratch
2. **Stay updated** — Censorship tactics evolve; keep software and configs current
3. **Operational security** — Use DNS-over-HTTPS, avoid public Wi‑Fi, and understand legal risks

## Legal Disclaimer

Unauthorized VPN use is illegal in Iran (Supreme Council of Cyberspace, Feb 2024). This report is for informational and research purposes only. Users assume all legal and personal risks. See [05-recommendations.md](05-recommendations.md) for detailed safety guidance.
