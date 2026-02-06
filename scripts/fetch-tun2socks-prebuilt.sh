#!/bin/bash
# Fetches prebuilt badvpn-tun2socks libs from universal-android-tun2socks.
# Run from project root before building Android app.

set -e
BASE="https://raw.githubusercontent.com/mokhtarabadi/universal-android-tun2socks/main/app/src/main/cpp/prebuilt"
DEST="android/app/src/main/cpp/prebuilt"
mkdir -p "$DEST/include"
mkdir -p "$DEST/lib/arm64-v8a" "$DEST/lib/armeabi-v7a" "$DEST/lib/x86" "$DEST/lib/x86_64"

echo "Fetching tun2socks headers..."
curl -sL "$BASE/include/tun2socks/tun2socks.h" -o "$DEST/include/tun2socks.h"
mkdir -p "$DEST/include/tun2socks"
mv "$DEST/include/tun2socks.h" "$DEST/include/tun2socks/"

echo "Fetching prebuilt libs..."
for abi in arm64-v8a armeabi-v7a x86 x86_64; do
  curl -sL "$BASE/lib/$abi/libtun2socks.a" -o "$DEST/lib/$abi/libtun2socks.a"
  echo "  $abi done"
done
echo "Done. Prebuilts in $DEST"
