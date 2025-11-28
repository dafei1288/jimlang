#!/bin/bash
# JimLang Unix/Linux/Mac 启动脚本
#
# 使用方式:
#   jimlang script.jim
#   jimlang --help
#   jimlang --version

# 获取脚本所在目录（bin 目录）
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 设置 JAR 文件路径（位于 target 目录）
JIMLANG_JAR="${SCRIPT_DIR}/../target/jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar"

# 检查 JAR 文件是否存在
if [ ! -f "$JIMLANG_JAR" ]; then
    echo "Error: JimLang JAR file not found!"
    echo "Expected location: $JIMLANG_JAR"
    echo ""
    echo "Please build the project first:"
    echo "  cd ${SCRIPT_DIR}/.."
    echo "  mvn clean package"
    exit 1
fi

# 检查 Java 是否安装
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 21 or later"
    exit 1
fi

# 执行 JimLang
java -jar "$JIMLANG_JAR" "$@"
