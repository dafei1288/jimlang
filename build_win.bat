@echo off
  setlocal EnableExtensions EnableDelayedExpansion

  REM Base dir
  set "PROJ=%~dp0"
  cd /d "%PROJ%" || (echo [ERR] Cannot cd to "%PROJ%" & exit /b 1)

  REM Check tools
  where mvn >nul 2>&1 || (echo [ERR] Maven not found in PATH & exit /b 1)

  REM Resolve JAVA_HOME/jdeps/jlink
  if not defined JAVA_HOME (
    for /f "delims=" %%J in ('where java 2^>nul') do set "_JAVA_EXE=%%~fJ"
    if defined _JAVA_EXE for %%D in ("!_JAVA_EXE!\..") do set "JAVA_HOME=%%~fD\.."
  )
  set "JDEPS=%JAVA_HOME%\bin\jdeps.exe"
  set "JLINK=%JAVA_HOME%\bin\jlink.exe"
  if not exist "%JDEPS%" set "JDEPS=jdeps"
  if not exist "%JLINK%" set "JLINK=jlink"

  echo [INFO] JAVA_HOME=%JAVA_HOME%
  echo [INFO] jdeps="%JDEPS%"
  echo [INFO] jlink="%JLINK%"

  REM Build fat jar
  echo [STEP] mvn -q -DskipTests package
  call mvn -q -DskipTests package
  if errorlevel 1 (echo [ERR] Maven build failed & exit /b 1)

  REM Locate fat-jar
  set "JAR="
  for /f "delims=" %%F in ('dir /b /a:-d /o:-d "target\*jar-with-dependencies.jar"') do (
    if not defined JAR set "JAR=target\%%F"
  )
  if not defined JAR (
    echo [ERR] No *-jar-with-dependencies.jar in target\
    exit /b 1
  )
  echo [INFO] JAR=!JAR!

  REM Derive modules via jdeps (ignore missing deps)
  set "MODS="
  for /f "usebackq delims=" %%M in (`"%JDEPS%" --ignore-missing-deps --multi-release 21 --print-module-deps "!JAR!" 2^>NUL`) do (
    set "MODS=%%M"
  )
  if not defined MODS (
    set "MODS=java.base"
    echo [WARN] jdeps produced no modules; fallback to !MODS!
  )

  REM Optional modules
  if defined WITH_WEB (
    echo [INFO] WITH_WEB set; +jdk.httpserver
    set "MODS=!MODS!,jdk.httpserver"
  )
  if defined WITH_SQL (
    echo [INFO] WITH_SQL set; +java.sql
    set "MODS=!MODS!,java.sql"
  )
  if defined EXTRA_MODULES (
    echo [INFO] EXTRA_MODULES=!EXTRA_MODULES!
    set "MODS=!MODS!,!EXTRA_MODULES!"
  )

  REM Normalize (strip spaces)
  set "MODS=!MODS: =!"
  echo [INFO] Modules: !MODS!

  REM Output dir
  set "OUT=%PROJ%build\jre-jim"
  if defined JRE_OUT set "OUT=%JRE_OUT%"
  if exist "%OUT%" rmdir /s /q "%OUT%"
  mkdir "%OUT%" || (echo [ERR] Cannot create "%OUT%" & exit /b 1)

  REM jlink
  echo [STEP] jlink -> "%OUT%"
  "%JLINK%" --no-header-files --no-man-pages --strip-debug --compress=2 --add-modules !MODS! --output "%OUT%"
  if errorlevel 1 (
    echo [WARN] jlink failed with modules !MODS!. Retrying with java.base...
    rmdir /s /q "%OUT%" 2>nul
    "%JLINK%" --no-header-files --no-man-pages --strip-debug --compress=2 --add-modules java.base --output "%OUT%"
    if errorlevel 1 (echo [ERR] jlink fallback failed & exit /b 1)
    set "MODS=java.base"
  )

  echo.
  echo [DONE]
  echo   JAR: !JAR!
  echo   JRE: %OUT%
  echo   Modules: !MODS!
  echo.
  echo [NEXT] Copy the JRE folder and the JAR to your Linux/WSL box to assemble initramfs.
  exit /b 0
