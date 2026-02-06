# Newest VPN & Circumvention Technologies on GitHub — 2026 Report

**Report Date:** February 2026  
**Scope:** Latest open source VPN, proxy, and censorship-circumvention technologies on GitHub

---

## 1. Executive Summary

The VPN and circumvention ecosystem has evolved rapidly. New protocols emphasize **TLS fingerprint evasion**, **QUIC-based transport**, **peer-to-peer volunteer networks**, and **obfuscated WireGuard**. Leading platforms include **sing-box** (~30k stars), **Hiddify** (~25k stars), **Xray-core** (~47k stars), and **Hysteria 2**. Volunteer systems like **Snowflake** (USENIX Security 2024), **Psiphon Conduit**, **Lantern Unbounded**, and **Geph** provide resilient access during severe shutdowns. This report catalogs the newest technologies, their GitHub locations, and suitability for Iran and other censored regions.

---

## 2. Newest Protocols

### 2.1 Hysteria 2

| Attribute | Value |
|-----------|-------|
| **GitHub** | [github.com/apernet/hysteria](https://github.com/apernet/hysteria) |
| **License** | MIT |
| **Language** | Go |

**Description:** High-performance, censorship-resistant proxy using a customized QUIC protocol. Traffic masquerades as standard **HTTP/3**, making it harder for DPI to detect.

**Features:**
- Modes: SOCKS5, HTTP proxy, TCP/UDP forwarding, Linux TProxy, TUN
- Custom authentication, traffic stats, access control
- Cross-platform; actively maintained

**Censorship resistance:** Strong; HTTP/3 mimicry reduces fingerprinting.

---

### 2.2 TUIC (Tuic)

| Attribute | Value |
|-----------|-------|
| **GitHub** | [github.com/tuic-protocol/tuic](https://github.com/tuic-protocol/tuic) |
| **Stars** | ~3.1k |
| **License** | GPL-3.0 |

**Description:** "Delicately-TUICed 0-RTT proxy protocol" — minimalistic proxy with 0-RTT (zero round-trip) for low latency.

**Features:**
- TUIC v5 protocol
- 0-RTT connection establishment
- IPv6 UDP support; dynamic stream capacity
- Written in Rust

**Censorship resistance:** QUIC-based; less fingerprinting than traditional VPNs.

---

### 2.3 VLESS + Reality

| Attribute | Value |
|-----------|-------|
| **GitHub (Reality)** | [github.com/XTLS/REALITY](https://github.com/XTLS/REALITY) |
| **Protocol** | VLESS (stateless, lightweight) + Reality (TLS camouflage) |

**Description:** Reality uses a camouflage website's SNI so traffic appears as legitimate HTTPS to sites like Google or Apple. VLESS adds only 25–50 bytes overhead vs OpenVPN's 100+ bytes.

**Why it works against DPI:**
- No distinctive patterns for fingerprinting
- Resists JA3, SNI filtering, active probing
- Traffic looks like normal HTTPS

**Deployment:** Xray-core, sing-box; deployable on Linux, Docker, OpenWRT.

---

### 2.4 xHTTP

**Description:** Transport protocol in Xray-core; HTTP/2-based. Part of the evolution V2Ray → Xray → sing-box.

**Use case:** Alternative transport when WebSocket or gRPC are blocked or degraded.

---

### 2.5 ShadowTLS

| Attribute | Value |
|-----------|-------|
| **GitHub** | [github.com/ihciah/shadow-tls](https://github.com/ihciah/shadow-tls) |
| **Stars** | ~2.6k |
| **Language** | Rust |
| **License** | GPL |

**Description:** TLS proxy that exposes real TLS handshakes to firewalls. Masks traffic as legitimate TLS.

**Features:**
- Client and server modes
- Multiple SNI support; configurable ALPN
- V2 and V3 protocol; SIP003 plugin mode
- Latest: v0.2.25 (Dec 2024)

---

### 2.6 NaiveProxy

| Attribute | Value |
|-----------|-------|
| **GitHub** | [github.com/klzgrad/naiveproxy](https://github.com/klzgrad/naiveproxy) |
| **Stars** | ~7.5k |
| **Description** | Uses Chromium's network stack; Caddy 2 ForwardProxy |

**Why it works:** Mitigates TLS fingerprint detection; has survived large-scale blocking in China. Requires NaiveProxy client + Caddy server with ForwardProxy module.

---

## 3. Universal Proxy Platforms

### 3.1 sing-box

| Attribute | Value |
|-----------|-------|
| **GitHub** | [github.com/SagerNet/sing-box](https://github.com/SagerNet/sing-box) |
| **Stars** | ~30k |
| **License** | GPL-3.0 |
| **Docs** | [sing-box.sagernet.org](https://sing-box.sagernet.org) |

**Description:** Universal proxy platform written in Go. Supports many protocols and transports.

**Supported:**
- QUIC, HTTP3 DNS; WireGuard outbound
- Clash API, V2Ray API
- ACME TLS; Tor outbound
- NaiveProxy outbound; gVisor

**Build:** Go 1.23.1+; optional build tags for features.

---

### 3.2 Xray-core

| Attribute | Value |
|-----------|-------|
| **GitHub** | [github.com/XTLS/Xray-core](https://github.com/XTLS/Xray-core) |
| **Stars** | ~47k |
| **Description** | Fork of V2Ray; XTLS, REALITY, VLESS, VMess, Trojan |

**Evolution:** XTLS (kernel-level connection splicing) replaces TLS-in-TLS; REALITY uses legitimate site identities. Better performance than V2Ray.

---

### 3.3 Hiddify

| Attribute | Value |
|-----------|-------|
| **GitHub** | [github.com/hiddify/hiddify-app](https://github.com/hiddify/hiddify-app) |
| **Stars** | ~25k |
| **Platforms** | Windows, macOS, Linux, Android, iOS |

**Description:** Multi-platform proxy client. Supports Sing-box, Xray, TUIC, Hysteria, Reality, Trojan, SSH, VLESS, VMess. 100% open source; ad-free.

**Related:** HiddifyManager (multi-user panel); Hiddify-Reality-Scanner.

---

### 3.4 v2ray-agent

| Attribute | Value |
|-----------|-------|
| **GitHub** | [github.com/v2ray-agent](https://github.com) (search: v2ray-agent) |
| **Stars** | ~18k |
| **Description** | One-click multi-protocol installation script including Hysteria2, Xray, etc. |

---

## 4. Volunteer / Peer-to-Peer Systems

### 4.1 Snowflake (Tor)

| Attribute | Value |
|-----------|-------|
| **GitLab** | [gitlab.com/torproject/snowflake](https://gitlab.com/torproject/snowflake) |
| **Paper** | USENIX Security 2024 |

**Description:** Temporary WebRTC proxies in JavaScript. Large pool of cheap, disposable proxies; resistant to enumeration and blocking.

**Deployment:** Tor Browser, Orbot. Proven in Russia (2021), Iran (2022).

---

### 4.2 Psiphon Conduit

| Attribute | Value |
|-----------|-------|
| **GitHub** | [github.com/Psiphon-Inc/conduit](https://github.com/Psiphon-Inc/conduit) |
| **Website** | [conduit.psiphon.ca](https://conduit.psiphon.ca) |

**Description:** Volunteers run Conduit Stations; Psiphon users in censored regions connect via peer-to-peer tunnels. Iran: >50% of 2.8M connection attempts (Jan 2026).

---

### 4.3 Lantern Unbounded

| Attribute | Value |
|-----------|-------|
| **GitHub** | [github.com/getlantern/unbounded](https://github.com/getlantern/unbounded) |
| **License** | GPL-3.0 |
| **Website** | [unbounded.lantern.io](https://unbounded.lantern.io) |

**Description:** Turns browser into a "digital bridge." Volunteers in uncensored regions share bandwidth; traffic exits via Lantern servers. Max 5 concurrent connections per volunteer.

**Protocols:** Shadowsocks, ShadowTLS, TUIC, Hysteria 2, Trojan, Tor Pluggable Transports. Dynamic selection by region and censorship conditions.

---

### 4.4 Geph

| Attribute | Value |
|-----------|-------|
| **GitHub** | [geph-official/geph4-client](https://github.com/geph-official/geph4-client) |
| **Website** | [geph.io](https://geph.io) |

**Description:** "World's most resilient VPN." Two-hop architecture (bridge relays + exit nodes). Domain fronting; ScrambleSuit-style handshakes; blind signature authentication.

**Status:** Geph 4/5 active; older repos archived.

---

## 5. WireGuard Obfuscation & DPI Bypass

| Project | GitHub | Description |
|---------|--------|-------------|
| **RostamWG** | [RostamVPN/RostamWG](https://github.com/RostamVPN/RostamWG) | Obfuscated WireGuard for Iran DPI |
| **WireGuard DPI Converter** | [fevid/wireguard-dpi-circumvention-converter](https://github.com/fevid/wireguard-dpi-circumvention-converter) | Converts .conf to AmneziaWG, Clash, Wiresock with junk packets |
| **ClusterM/wg-obfuscator** | [ClusterM/wg-obfuscator](https://github.com/ClusterM/wg-obfuscator) | ~455 stars; simple WireGuard obfuscator |
| **WG-Fake** | [lastbyte32/wg-fake](https://github.com/lastbyte32/wg-fake) | Fake handshakes; "magic" packets for DPI bypass |
| **DPIMyAss** | [mrsobakin/dpimyass](https://github.com/mrsobakin/dpimyass) | Rust UDP proxy for DPI bypass |

---

## 6. AmneziaVPN

| Attribute | Value |
|-----------|-------|
| **GitHub** | [github.com/amnezia-vpn/amnezia-client](https://github.com/amnezia-vpn/amnezia-client) |
| **Stars** | ~9.9k |
| **License** | GPL-3.0 |
| **Platforms** | Windows, macOS, Linux, Android, iOS |

**Protocols:** OpenVPN, WireGuard, IKEv2, Cloak obfuscation, Shadowsocks, XRay. **AmneziaWG** — obfuscated WireGuard with junk packets (amneziawg-go, ~1.2k stars).

**Features:** Split tunneling; automatic Docker deployment; self-hosted.

---

## 7. Quick Reference Table

| Technology | GitHub / Source | Stars (approx) | Best For |
|------------|-----------------|----------------|----------|
| sing-box | SagerNet/sing-box | 30k | Universal proxy; multi-protocol |
| Xray-core | XTLS/Xray-core | 47k | VLESS, Reality, VMess, Trojan |
| Hiddify | hiddify/hiddify-app | 25k | Multi-platform client; many protocols |
| Hysteria 2 | apernet/hysteria | — | QUIC; HTTP/3 mimicry |
| TUIC | tuic-protocol/tuic | 3.1k | 0-RTT; minimal overhead |
| ShadowTLS | ihciah/shadow-tls | 2.6k | TLS masking |
| NaiveProxy | klzgrad/naiveproxy | 7.5k | Chromium stack; fingerprint evasion |
| AmneziaVPN | amnezia-vpn/amnezia-client | 9.9k | Self-hosted; AmneziaWG obfuscation |
| Psiphon Conduit | Psiphon-Inc/conduit | — | Volunteer P2P; Iran proven |
| Lantern Unbounded | getlantern/unbounded | — | Volunteer P2P; multi-protocol |
| Geph | geph-official/geph4-client | — | Resilient; two-hop |
| Snowflake | torproject/snowflake (GitLab) | — | WebRTC; Tor integration |
| RostamWG | RostamVPN/RostamWG | — | Iran; obfuscated WireGuard |

---

## 8. Recommendations for Iran

1. **Primary:** Psiphon + Conduit (volunteer-powered; proven in Jan 2026 shutdown)
2. **Advanced:** Hiddify or NikaNG with VLESS+Reality, Hysteria 2, or TUIC configs
3. **WireGuard users:** RostamWG or AmneziaWG for obfuscation
4. **Self-hosted:** sing-box or Xray-core with Reality on a VPS abroad
5. **Redundancy:** Install multiple tools; protocols rotate as censorship evolves

---

## 9. References

| Resource | URL |
|----------|-----|
| sing-box | https://sing-box.sagernet.org |
| Hysteria 2 | https://hysteria.network |
| Hiddify | https://hiddifynext.app |
| Geph | https://geph.io |
| Lantern Unbounded | https://unbounded.lantern.io |
| Psiphon Conduit | https://conduit.psiphon.ca |
| Snowflake | https://snowflake.torproject.org |
| USENIX Security 2024 (Snowflake) | freehaven.net/anonbib/cache/snowflake-sec2024.pdf |

See [06-references.md](06-references.md) for additional citations.
