#!/usr/bin/env bash
set -euo pipefail

SDK="${ANDROID_SDK:-/tmp/android-sdk}"
export JAVA_HOME="${JAVA_HOME:-/tmp/jdk17}"
export PATH="$JAVA_HOME/bin:$PATH"
BT="$SDK/build-tools/android-16"
PLATFORM="$SDK/platforms/android-35/android.jar"
ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/build"

rm -rf "$OUT"
mkdir -p "$OUT/gen" "$OUT/classes" "$OUT/dex"

echo "[1/8] aapt2 compile"
"$BT/aapt2" compile --dir "$ROOT/res" -o "$OUT/res.zip"

echo "[2/8] aapt2 link"
"$BT/aapt2" link -o "$OUT/base.apk" -I "$PLATFORM" \
  --manifest "$ROOT/AndroidManifest.xml" \
  --java "$OUT/gen" \
  "$OUT/res.zip"

echo "[3/8] javac"
find "$ROOT/src" "$OUT/gen" -name '*.java' > "$OUT/sources.txt"
javac -encoding UTF-8 -source 8 -target 8 -bootclasspath "$PLATFORM" \
  -d "$OUT/classes" @"$OUT/sources.txt"

echo "[4/8] d8"
"$BT/d8" --lib "$PLATFORM" --min-api 21 --output "$OUT/dex" \
  $(find "$OUT/classes" -name '*.class')

echo "[5/8] package dex"
cp "$OUT/base.apk" "$OUT/unsigned.apk"
(cd "$OUT/dex" && zip -q -u "$OUT/unsigned.apk" classes.dex)

echo "[6/8] zipalign"
"$BT/zipalign" -f 4 "$OUT/unsigned.apk" "$OUT/aligned.apk"

echo "[7/8] keystore"
if [ ! -f "$ROOT/debug.keystore" ]; then
  keytool -genkeypair -keystore "$ROOT/debug.keystore" -alias androiddebugkey \
    -storepass android -keypass android -keyalg RSA -keysize 2048 -validity 10000 \
    -dname "CN=Android Debug,O=Android,C=US"
fi

echo "[8/8] sign"
"$BT/apksigner" sign --ks "$ROOT/debug.keystore" --ks-pass pass:android --key-pass pass:android \
  --v4-signing-enabled false \
  --out "$ROOT/SteamGo-2.6.6.apk" "$OUT/aligned.apk"

echo "--- verify ---"
"$BT/apksigner" verify --print-certs "$ROOT/SteamGo-2.6.6.apk"
echo "APK: $ROOT/SteamGo-2.6.6.apk"
