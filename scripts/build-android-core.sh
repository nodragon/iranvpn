#!/usr/bin/env bash
# Build iran-vpn-core for Android (JNI .so files).
# Output: android/app/src/main/jniLibs/{abi}/libiran_vpn_core.so
# Requires: cargo, rustup, cargo-ndk (cargo install cargo-ndk), Android NDK
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CORE_DIR="$PROJECT_ROOT/core"
JNI_LIBS="$PROJECT_ROOT/android/app/src/main/jniLibs"

cd "$PROJECT_ROOT"

# Add Android targets if needed
rustup target add aarch64-linux-android armv7-linux-androideabi x86_64-linux-android i686-linux-android 2>/dev/null || true

# Build with cargo-ndk (preferred) or manual cargo
if command -v cargo-ndk &>/dev/null; then
  echo "Building with cargo-ndk..."
  cd "$PROJECT_ROOT"
  cargo ndk -t armeabi-v7a -t arm64-v8a -t x86 -t x86_64 \
    -o "$JNI_LIBS" \
    -p iran-vpn-core build --release --features jni
else
  echo "Building with cargo (set ANDROID_NDK_HOME for NDK path)..."
  mkdir -p "$JNI_LIBS"
  for target in aarch64-linux-android armv7-linux-androideabi i686-linux-android x86_64-linux-android; do
    cargo build -p iran-vpn-core --target "$target" --release --features jni
    # Map Rust targets to Android ABI dirs
    case "$target" in
      aarch64-linux-android) dir="arm64-v8a" ;;
      armv7-linux-androideabi) dir="armeabi-v7a" ;;
      i686-linux-android) dir="x86" ;;
      x86_64-linux-android) dir="x86_64" ;;
      *) dir="$target" ;;
    esac
    mkdir -p "$JNI_LIBS/$dir"
    cp "$PROJECT_ROOT/target/$target/release/libiran_vpn_core.so" "$JNI_LIBS/$dir/"
  done
fi

echo "Done. Built .so files in $JNI_LIBS"
