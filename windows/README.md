# Iran VPN — Windows

Windows tunnel app using `core` as native lib. Tunnel via Wintun.

## Stack

- **Rust** app with egui UI; links to `iran-vpn-core`.
- **Wintun:** user-space TUN; establish tunnel and forward to path (Psiphon/Conduit/Xray/Rostam).

## Build

```bash
cd windows
cargo build --release
```

Requires: Rust, Windows SDK. Wintun DLL is fetched automatically by the `wintun` crate.

## Run

```bash
cargo run -p iran-vpn-windows --release
```

**Demo mode (Wintun TUN only, no real backend):** `IRAN_VPN_DEMO=1 cargo run -p iran-vpn-windows --release`

## Distribution

US-005: ship installer via GitHub Releases and at least one mirror. Use WiX or NSIS for installer.
