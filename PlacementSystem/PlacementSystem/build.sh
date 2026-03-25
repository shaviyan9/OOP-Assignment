#!/bin/bash
# ─────────────────────────────────────────────────────────────────────────────
# build.sh  –  Compile and run the Placement System
# Usage: chmod +x build.sh && ./build.sh
# ─────────────────────────────────────────────────────────────────────────────

set -e

MYSQL_JAR=$(ls lib/mysql-connector-j-*.jar 2>/dev/null | head -1)

if [ -z "$MYSQL_JAR" ]; then
    echo "ERROR: MySQL Connector/J JAR not found in lib/"
    echo "Download it from: https://dev.mysql.com/downloads/connector/j/"
    echo "Place the JAR file in the lib/ directory and re-run."
    exit 1
fi

echo "Using: $MYSQL_JAR"

# Create output directory
mkdir -p out

# Find all .java source files
SRC_FILES=$(find src -name "*.java" | tr '\n' ' ')

echo "Compiling..."
javac -cp "$MYSQL_JAR" -d out $SRC_FILES

if [ $? -eq 0 ]; then
    echo "Compilation successful."
    echo "Starting application..."
    java -cp "out:$MYSQL_JAR" Main
else
    echo "Compilation failed."
    exit 1
fi
