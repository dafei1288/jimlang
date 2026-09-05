#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR="$DIR/../target/jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar"
if [ ! -f "$JAR" ]; then
  echo "LSP jar not found: $JAR" >&2
  echo "Build with: mvn -q -DskipTests package" >&2
  exit 1
fi
exec java -cp "$JAR" com.dafei1288.jimlang.lsp.LspServerLauncher