@echo off
REM JimLang Windows launcher script
REM
REM Usage:
REM   jimlang.cmd script.jim
REM   jimlang.cmd --help
REM   jimlang.cmd --version

setlocal

REM Resolve script directory (bin)
set SCRIPT_DIR=%~dp0

REM Set JAR path (under target)
set JIMLANG_JAR=%SCRIPT_DIR%..\target\jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar

REM Check JAR exists
if not exist "%JIMLANG_JAR%" (
    echo Error: JimLang JAR file not found!
    echo Expected location: %JIMLANG_JAR%
    echo.
    echo Please build the project first:
    echo   cd %SCRIPT_DIR%..
    echo   mvn clean package
    exit /b 1
)

REM Check Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 21 or later
    exit /b 1
)

REM Run JimLang
java -jar "%JIMLANG_JAR%" %*

endlocal
