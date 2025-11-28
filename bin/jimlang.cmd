@echo off
REM JimLang Windows 启动脚本
REM
REM 使用方式:
REM   jimlang.cmd script.jim
REM   jimlang.cmd --help
REM   jimlang.cmd --version

setlocal

REM 获取脚本所在目录（bin 目录）
set SCRIPT_DIR=%~dp0

REM 设置 JAR 文件路径（位于 target 目录）
set JIMLANG_JAR=%SCRIPT_DIR%..\target\jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar

REM 检查 JAR 文件是否存在
if not exist "%JIMLANG_JAR%" (
    echo Error: JimLang JAR file not found!
    echo Expected location: %JIMLANG_JAR%
    echo.
    echo Please build the project first:
    echo   cd %SCRIPT_DIR%..
    echo   mvn clean package
    exit /b 1
)

REM 检查 Java 是否安装
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 21 or later
    exit /b 1
)

REM 执行 JimLang
java -jar "%JIMLANG_JAR%" %*

endlocal
