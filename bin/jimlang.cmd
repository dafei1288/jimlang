@echo off
REM JimLang Windows launcher script with /ext classpath support
REM
REM Usage:
REM   jimlang.cmd script.jim
REM   jimlang.cmd --help
REM   jimlang.cmd --version

setlocal EnableDelayedExpansion

REM Resolve script directory (bin)
set "SCRIPT_DIR=%~dp0"

REM Set JAR path (under target)
set "JIMLANG_JAR=%SCRIPT_DIR%..\target\jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar"

REM Optional external jars directory
set "EXT_DIR=%SCRIPT_DIR%..\ext"

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

REM Build classpath: shaded jar + ext/*.jar (recursive)
set "JIMLANG_CP=%JIMLANG_JAR%"
if exist "%EXT_DIR%" (
    for /R "%EXT_DIR%" %%F in (*.jar) do (
        set "JIMLANG_CP=!JIMLANG_CP!;%%~fF"
    )
)

REM Run JimLang (use -cp so ext jars are visible)
java -cp "%JIMLANG_CP%" com.dafei1288.jimlang.Main %*

endlocal