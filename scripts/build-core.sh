#!/usr/bin/env bash
# Build iran-vpn-core for all targets (Phase 1.3).
# Requires: rustup, Android NDK for android targets, Xcode for ios/macos.
set -e
cd "$(dirname "$0")/.."

echo "Building core for host..."
cargo build -p iran-vpn-core

echo "Building core for Windows (x86_64-pc-windows-msvc)..."
cargo build -p iran-vpn-core --target x86_64-pc-windows-msvc

echo "Building core for macOS (aarch64-apple-darwin, x86_64-apple-darwin)..."
cargo build -p iran-vpn-core --target aarch64-apple-darwin 2>/dev/null || true
cargo build -p iran-vpn-core --target x86_64-apple-darwin 2>/dev/null || true

echo "Building core for iOS (aarch64-apple-ios, x86_64-apple-ios)..."
rustup target add aarch64-apple-ios x86_64-apple-ios 2>/dev/null || true
cargo build -p iran-vpn-core --target aarch64-apple-ios --no-default-features 2>/dev/null || true
cargo build -p iran-vpn-core --target x86_64-apple-ios --no-default-features 2>/dev/null || true

echo "Android targets require NDK. Add with: rustup target add aarch64-linux-android armv7-linux-androideabi i686-linux-android x86_64-linux-android"
echo "Then set NDK path and: cargo build -p iran-vpn-core --target aarch64-linux-android"

echo "Done. Core built for host and Windows."
