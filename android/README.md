# Iran VPN — Android

Android app with fallback chain (Psiphon → Conduit → Xray → Rostam).

## Build

1. **Android SDK:** Set `ANDROID_HOME` or create `local.properties` with `sdk.dir=/path/to/Android/sdk`. Copy from `local.properties.example`.
2. **tun2socks prebuilts (required):** From project root, run `./scripts/fetch-tun2socks-prebuilt.sh` to download badvpn-tun2socks libs. Run once before first build.
3. **Rust core (optional):** From project root, run `./scripts/build-android-core.sh` to build the native library. Without it, config fetch uses Java HTTP.
4. **Open in Android Studio** or run `./gradlew assembleDebug`.

Build uses Gradle 9.3.1 and AGP 8.7.2; compatible with Java 17–25.

## Psiphon integration

PsiphonPathRunner uses reflection so the app builds and runs without the Psiphon SDK. To enable full Psiphon:

1. Uncomment in `app/build.gradle.kts`:
   ```kotlin
   implementation("ca.psiphon:psiphontunnel:2.0.35")
   ```
2. The Psiphon Maven repo is already in `settings.gradle.kts`.
3. Build with Java 17 for best compatibility.

Without the dependency, selecting Psiphon path returns "Psiphon SDK not on classpath".

## Xray integration

XrayPathRunner runs xray as a subprocess. Add the xray binary to assets:

- `assets/xray/xray` (single binary), or
- `assets/xray/<abi>/xray` (e.g. `assets/xray/arm64-v8a/xray`)

Download from [Xray-core releases](https://github.com/XTLS/Xray-core/releases); extract the appropriate binary. Config is written from `PathConfig.XrayConfig`; supports subscription URL fetch.

## Packet forwarding (tun2socks)

TUN traffic is routed through SOCKS5 via badvpn-tun2socks (from universal-android-tun2socks).

**First-time setup:** Run from project root:
```bash
./scripts/fetch-tun2socks-prebuilt.sh
```
This downloads prebuilt libtun2socks.a for arm64-v8a, armeabi-v7a, x86, x86_64.

When Psiphon or Xray provides a local SOCKS5 proxy, `PacketForwarder` starts tun2socks with the TUN fd and SOCKS port. Xray supports UDP (`--socks5-udp`); Psiphon uses TCP only.
