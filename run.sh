#!/bin/bash

# Battleship Game Launcher Script
# This script sets up the environment and runs the Battleship game

echo " Battleship Game Launcher"
echo "=========================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo " Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or later"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo " Error: Java 17 or later is required. Found version: $JAVA_VERSION"
    exit 1
fi

echo " Java version: $(java -version 2>&1 | head -n 1)"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo " Error: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6 or later"
    exit 1
fi

echo " Maven version: $(mvn -version | head -n 1)"

# Set JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null)
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    fi
    
    if [ -n "$JAVA_HOME" ]; then
        export JAVA_HOME
        echo " Set JAVA_HOME to: $JAVA_HOME"
    fi
fi

# Check if JAR exists, if not build it
if [ ! -f "target/battleship-game-1.0.0.jar" ]; then
    echo " Building the game..."
    # Ensure JAVA_HOME is set for Maven
    export JAVA_HOME="$JAVA_HOME"
    mvn clean package -q
    if [ $? -ne 0 ]; then
        echo " Error: Failed to build the project"
        exit 1
    fi
    echo " Build completed successfully"
fi

echo " Starting Battleship Game..."
echo "=========================="

# Run the game
java -jar target/battleship-game-1.0.0.jar 