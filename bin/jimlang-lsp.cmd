@echo off
setlocal
set SCRIPT_DIR=%~dp0
set JAR=%SCRIPT_DIR%..\target\jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar
if not exist "%JAR%" (
  echo LSP jar not found: %JAR%
  echo Build with: mvn -q -DskipTests package
  exit /b 1
)
java -cp "%JAR%" com.dafei1288.jimlang.lsp.LspServerLauncher