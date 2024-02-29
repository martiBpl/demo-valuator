#!/usr/bin/env sh

# INPUT: Path to Spring Boot's fat jar
# OUTPUT: Comma-delimited JDK modules the fat JAR depends on

set -e

DEPS_DIR="$TMPDIR/analyze_deps"
clean_up () {
    rm -Rf ./analyze_deps
}
trap clean_up INT TERM EXIT

mkdir -p "$DEPS_DIR"
JAR_PATH="$PWD"/${1:?'Spring Boot JAR path missing'}
cd "$DEPS_DIR" && jar xvf "$JAR_PATH" > /dev/null && cd ..
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
jdeps \
  --print-module-deps \
  --ignore-missing-deps \
  --recursive \
  --multi-release "$JAVA_VERSION" \
  --class-path="$DEPS_DIR/BOOT-INF/lib/*" \
  --module-path="$DEPS_DIR/BOOT-INF/lib/*" \
  "$JAR_PATH"
