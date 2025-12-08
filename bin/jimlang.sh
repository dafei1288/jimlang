#!/bin/bash
# JimLang Unix/Linux/Mac launcher with /ext classpath support
#
# Usage:
#   jimlang script.jim
#   jimlang --help
#   jimlang --version

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JIMLANG_JAR="${SCRIPT_DIR}/../target/jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar"
EXT_DIR="${SCRIPT_DIR}/../ext"

if [ ! -f "$JIMLANG_JAR" ]; then
  echo "Error: JimLang JAR file not found!"
  echo "Expected location: $JIMLANG_JAR"
  echo
  echo "Please build the project first:"
  echo "  cd ${SCRIPT_DIR}/.."
  echo "  mvn clean package"
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "Error: Java is not installed or not in PATH"
  echo "Please install Java 21 or later"
  exit 1
fi

# Build classpath: shaded jar + ext jars (recursive)
CP="$JIMLANG_JAR"
if [ -d "$EXT_DIR" ]; then
  while IFS= read -r -d ' j; do
    CP="$CP:$j"
  done < <(find "$EXT_DIR" -type f -name '*.jar' -print0 2>/dev/null)
fi

# Use -cp so ext jars are visible
exec java -cp "$CP" com.dafei1288.jimlang.Main "$@"
