# Complete Report: Iran Filtering, VPN Circumvention, Pros & Cons, and Improvements

**Report Date:** February 2026  
**Status:** Draft

---

## 1. Executive Summary

Iran maintains one of the world's most restrictive internet environments, combining DNS poisoning, Deep Packet Inspection (DPI), protocol whitelisting, and blockpage injection to control access. As of February 2024, unauthorized VPN use is illegal. Despite this, free and open source circumvention tools—notably **Psiphon** (including its volunteer-powered **Conduit** system), **MahsaNG**, **NikaNG**, and **RostamWG**—continue to provide access. This report documents Iran's filtering infrastructure, evaluates available tools, analyzes pros and cons, and recommends improvements for users, developers, and the circumvention ecosystem.

---

## 2. Iran's Filtering System

### 2.1 Technical Mechanisms

| Mechanism | Description |
|-----------|-------------|
| DNS poisoning | Affects millions of IPs; blocks domains at resolution |
| DPI | Detects VPN traffic by packet signatures; protocol whitelist (DNS, HTTP, HTTPS only) |
| HTTP blockpage injection | Injects blocking pages for HTTP requests |
| HTTPS/SNI filtering | Blocks by TLS SNI inspection |
| UDP disruption | Blocks or degrades UDP-based protocols |
| Protocol whitelist | Only DNS, HTTP, HTTPS permitted |

### 2.2 Scale

- Over 6 million fully qualified domain names blocked
- Over 3.3 million apex domains blocked
- Blocked: social media, messaging apps, news outlets, encrypted DNS

### 2.3 Legal Context

- Unauthorized VPN use illegal (Supreme Council of Cyberspace, Feb 2024)
- Tiered internet: preferential access for approved groups; stricter filtering for general public

---

## 3. Circumvention Solutions

### 3.1 Psiphon (Main App)

Direct-to-Psiphon VPN. Users connect to Psiphon servers via obfuscated SSH and HTTP prefixes.

### 3.2 Psiphon Conduit (Volunteer-Powered)

**Conduit** is a peer-to-peer proxy system where volunteers run "Conduit Stations" to share bandwidth with Psiphon users in censored regions.

**How it works:**
1. Volunteer installs Conduit on phone/PC and runs it (Android: can run in background).
2. Psiphon user in Iran requests a site.
3. Connection is established via peer-to-peer tunnel to a Conduit Station.
4. Traffic is routed through Psiphon servers to the open internet.

**Key facts:**
- Open source: [github.com/Psiphon-Inc/conduit](https://github.com/Psiphon-Inc/conduit)
- Platforms: Android, Windows, macOS (iOS planned)
- Security: Cure53 audit; all traffic encrypted; volunteers cannot see content
- Iran usage (Jan 2026): >50% of 2.8M Conduit connection attempts from Iran; 40,000+ simultaneous users

**Sources:** [conduit.psiphon.ca](https://conduit.psiphon.ca/), [Iran International](https://www.iranintl.com/en/202601240957)

### 3.3 Other Tools

- **MahsaNG:** Iran-focused; built-in configs; Android
- **NikaNG:** v2rayNG fork; WARP, WireGuard Noise, Fragment; Android
- **RostamWG:** Obfuscated WireGuard; cross-platform
- **WARP+Psiphon:** Combines WARP and Psiphon
- **Outline, Xray, V2Ray:** Self-hosted; protocol stacks

---

## 4. Pros and Cons

### 4.1 Psiphon (Main App)

| Pros | Cons |
|------|------|
| No configuration; works out of the box | Centralized servers can be targeted |
| Long track record (since 2006) | Speed and reliability vary by server |
| No user accounts; no address logging | Dependent on Psiphon Inc. availability |
| Obfuscated SSH; DPI-resistant | May be blocked during severe shutdowns |
| Windows, Android, iOS | |

### 4.2 Psiphon Conduit

| Pros | Cons |
|------|------|
| Exploits pathways that cannot be fully closed without disrupting state systems | Low speeds (~25 users per volunteer) |
| Traffic disguised as ordinary web activity | Requires volunteer participation |
| Distributed; no single point of failure | Battery and data usage on volunteer devices |
| Works during severe shutdowns when direct Psiphon may fail | Connections can drop and reconnect |
| Open source; Cure53 audited | No iOS yet |
| Volunteers cannot see content; strong privacy | Demand can exceed supply |
| Iran has more Psiphon users than any other country | |

### 4.3 MahsaNG

| Pros | Cons |
|------|------|
| Built-in configs; Iran-focused | Android only |
| Easy for non-technical users | Dependent on Mahsa Server availability |
| Active development | Single-ecosystem risk |

### 4.4 NikaNG

| Pros | Cons |
|------|------|
| Multiple protocols (WARP, WG Noise, Fragment, xhttp, QUIC, Hy2) | No built-in configs; user must obtain |
| Advanced users; highly configurable | Android only |
| Iran-specific optimizations | Steeper learning curve |

### 4.5 RostamWG

| Pros | Cons |
|------|------|
| Obfuscated WireGuard; DPI bypass | Requires server setup or third-party configs |
| Cross-platform | Smaller community than Psiphon/MahsaNet |
| Designed for Iran | |

### 4.6 WARP+Psiphon

| Pros | Cons |
|------|------|
| Combines two approaches | Cloudflare WARP policies may restrict abuse |
| Active community forks | Reliability mixed; CDN abuse flagging |
| MIT licensed | |

### 4.7 Self-Hosted (Outline, Xray, V2Ray)

| Pros | Cons |
|------|------|
| Full control; no third-party dependency | Requires technical skill and VPS |
| Censorship-resistant designs | Server IPs can be blocked |
| Bandwidth limits per user | Costs for hosting |

---

## 5. What Can Be Improved

### 5.1 For Conduit

| Area | Improvement |
|------|-------------|
| **iOS support** | Conduit is not yet on iOS; expanding to iOS would allow more volunteers. |
| **Throughput** | ~25 users per volunteer at low speeds; research into more efficient multiplexing or protocol tweaks could increase capacity. |
| **Battery usage** | Optimize networking components to reduce power consumption on volunteer devices. |
| **Matching algorithm** | Improve pairing of users with geographically or latency-optimal stations. |
| **Link distribution** | Enhance private link distribution and rotation to reduce blocking. |
| **Visibility** | More public stats, success metrics, and volunteer onboarding to grow the network. |
| **Fallback chains** | Automatic failover between Conduit and direct Psiphon when one path fails. |

### 5.2 For Psiphon (Main App)

| Area | Improvement |
|------|-------------|
| **Server diversity** | More server locations and protocols to reduce single-point dependency. |
| **Shutdown resilience** | Tighter integration with Conduit so users seamlessly use Conduit when direct access fails. |
| **Protocol evolution** | Continuous obfuscation updates as Iran's DPI evolves. |
| **Download availability** | Ensure installers and updates remain obtainable during outages (e.g., alternate distribution). |

### 5.3 For Iran-Specific Tools (MahsaNG, NikaNG)

| Area | Improvement |
|------|-------------|
| **Multi-platform** | Extend to iOS and desktop to reach more users. |
| **Config distribution** | Decentralized or redundant config distribution to reduce blocking. |
| **Protocol agility** | Quick adoption of new protocols (e.g., Hy2, xHTTP) as censorship tactics change. |
| **Documentation** | Clear setup guides for non-Persian speakers and developers. |

### 5.4 For the Broader Ecosystem

| Area | Improvement |
|------|-------------|
| **Interoperability** | Shared config formats and fallback between tools (e.g., Psiphon → Conduit → NikaNG). |
| **Measurement** | More OONI-style measurements and Filter Watch integration for real-time blocking visibility. |
| **Legal awareness** | Clear, localized guidance on legal risks for users. |
| **Funding** | Sustainable funding for infrastructure and audits (Psiphon, MahsaNet, etc.). |
| **Coordination** | Coordination during shutdowns (volunteer drives, status pages, alternate distribution). |
| **Security audits** | Regular third-party audits for all major tools. |

### 5.5 For Users

| Area | Improvement |
|------|-------------|
| **Pre-installation** | Install tools before a shutdown; once blocked, downloading is much harder. |
| **Multiple tools** | Keep 2–3 tools installed with different protocols for redundancy. |
| **Operational security** | Use DNS-over-HTTPS where possible; avoid public Wi‑Fi; keep software updated. |
| **Volunteer recruitment** | Diaspora and allies can run Conduit to strengthen the network. |

---

## 6. Recommendations Summary

### For Users in Iran

1. Install Psiphon and at least one Iran-specific client (e.g., MahsaNG or NikaNG) before access is restricted.
2. Understand legal risks; this report is informational only.
3. Use multiple tools; if one fails, try another.
4. Stay updated; censorship tactics evolve.

### For Volunteers Abroad

1. Run [Conduit](https://conduit.psiphon.ca/) on spare devices; each station can support ~25 users.
2. Use stable connections (home Wi‑Fi, unlimited plans) where possible.
3. Adjust settings to manage bandwidth and battery.

### For Developers and Maintainers

1. Prioritize Conduit iOS support and throughput optimization.
2. Improve interoperability and fallback between tools.
3. Maintain regular security audits.
4. Ensure robust config and installer distribution during shutdowns.

---

## 7. Legal Disclaimer

This report is for informational and research purposes only. Unauthorized VPN use is illegal in Iran. Users assume all legal and personal risks. The authors and publishers assume no liability for actions taken based on this document.

---

## 8. References

| Source | URL |
|--------|-----|
| Conduit | https://conduit.psiphon.ca/ |
| Conduit GitHub | https://github.com/Psiphon-Inc/conduit |
| Iran International (Conduit in Iran) | https://www.iranintl.com/en/202601240957 |
| Psiphon | https://psiphon.ca |
| OONI Iran | https://explorer.ooni.org/country/IR |
| Freedom House Iran | https://freedomhouse.org/country/iran/freedom-net/2025 |
| MahsaNet | https://mahsanet.com |

See [06-references.md](06-references.md) for full citations.
