#!/usr/bin/env sh

set -e

APP_PATH=${1:?'APP path missing'}
OUTPUT=${2:?'Output path missing'}

APP_DEPS=$(./analyze-deps.sh "$APP_PATH")
DEPS=$(echo "$APP_DEPS" | awk 'BEGIN{RS=ORS=","} !seen[$0]++' | head -n 1)
echo "JDK modules to include in JRE: ${DEPS}"
jlink \
  --bind-services \
  --add-modules "$DEPS" \
  --strip-debug \
  --no-man-pages \
  --no-header-files \
  --compress=2 \
  --output "$OUTPUT"
