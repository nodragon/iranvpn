# Recommendations

## User Guidance

### 1. Use Existing Tools

Do not build a VPN from scratch. Deploy and document proven open source tools:

- **Psiphon** — Easiest; no configuration; works out of the box
- **MahsaNG** — Iran-focused; built-in configs; Android
- **NikaNG** — Advanced; multiple protocols; Iran-focused; Android
- **RostamWG** — Obfuscated WireGuard; cross-platform
- **WARP+Psiphon** — Combined approach when individual tools are blocked

### 2. Stay Updated

Censorship tactics evolve. Regularly:

- Update circumvention software
- Refresh configs and server lists
- Monitor OONI, Filter Watch, and community channels for changes

### 3. Operational Security

- **DNS-over-HTTPS (DoH):** Use DoH to avoid DNS poisoning (note: DoT/DoH may be blocked in Iran; tools like Psiphon handle DNS internally)
- **Avoid public Wi‑Fi:** Public networks are easier to monitor
- **Keep software updated:** Patches address new blocking methods
- **Use trusted sources:** Download only from official sites (e.g., psiphon.ca, mahsanet.com, GitHub releases)

### 4. Understand Legal Risks

- Unauthorized VPN use is **illegal** in Iran (Supreme Council of Cyberspace, February 2024)
- Penalties can be severe
- This report does not encourage illegal activity; it provides information for research and awareness

---

## Building or Deploying an Open Source VPN

### Option A: Use Existing Projects (Recommended)

- Deploy Psiphon, MahsaNG, NikaNG, RostamWG, or WARP+Psiphon
- Focus on documentation, distribution, and user support
- Minimal development required

### Option B: Customize and Extend

- Fork NikaNG, RostamWG, or Xray-core
- Add Iran-specific configs, protocol tweaks, or obfuscation
- Requires C/Rust/Go and protocol expertise

### Option C: New Application

- Build a client that wires together Xray/Rostam/Psiphon configs
- Higher effort; may duplicate existing work
- Not recommended given current tooling

---

## Legal Disclaimer

This report is for **informational and research purposes only**. It does not constitute legal advice. Users are solely responsible for understanding and complying with applicable laws. Unauthorized VPN use in Iran may result in legal consequences. The authors and publishers assume no liability for any actions taken based on this document.

---

## References

See [06-references.md](06-references.md) for citations.
