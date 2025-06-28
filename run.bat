@echo off
REM Battleship Game Launcher Script for Windows
REM This script sets up the environment and runs the Battleship game

echo 🚢 Battleship Game Launcher
echo ==========================

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Java is not installed or not in PATH
    echo Please install Java 17 or later
    pause
    exit /b 1
)

echo ✅ Java version:
java -version 2>&1 | findstr "version"

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Maven is not installed or not in PATH
    echo Please install Maven 3.6 or later
    pause
    exit /b 1
)

echo ✅ Maven version:
mvn -version | findstr "Apache Maven"

REM Check if JAR exists, if not build it
if not exist "target\battleship-game-1.0.0.jar" (
    echo 📦 Building the game...
    mvn clean package -q
    if %errorlevel% neq 0 (
        echo ❌ Error: Failed to build the project
        pause
        exit /b 1
    )
    echo ✅ Build completed successfully
)

echo 🎮 Starting Battleship Game...
echo ==========================

REM Run the game
java -jar target\battleship-game-1.0.0.jar

pause 