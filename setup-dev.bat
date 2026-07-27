@echo off
setlocal
cd /d "%~dp0"
echo ============================================
echo   CRM - One-click Dev Setup (Windows)
echo   Installs Java 17 if missing, so you can
echo   continue development on this computer.
echo ============================================
echo.

REM ---- 1. Check for Java 17 ----
set "JV="
for /f "tokens=*" %%v in ('java -version 2^>^&1 ^| findstr /C:"version"') do set "JV=%%v"
echo Detected: %JV%
echo %JV% | findstr /C:"17." >nul
if %errorlevel%==0 (
    echo [OK] Java 17 already installed.
    goto AFTERJAVA
)

echo Java 17 not found. Installing Eclipse Temurin JDK 17 ...
where winget >nul 2>nul
if errorlevel 1 (
    echo [ERROR] winget not available on this Windows.
    echo Please install JDK 17 manually from https://adoptium.net/temurin/releases/?version=17
    pause
    exit /b 1
)
winget install --id EclipseAdoptium.Temurin.17.JDK -e --source winget --accept-package-agreements --accept-source-agreements
if errorlevel 1 (
    echo [ERROR] JDK install failed. Install manually from https://adoptium.net
    pause
    exit /b 1
)

:AFTERJAVA
REM ---- 2. Set MAVEN_OPTS (helps behind a corporate SSL proxy; harmless otherwise) ----
setx MAVEN_OPTS "-Djavax.net.ssl.trustStoreType=Windows-ROOT" >nul

echo.
echo ============================================
echo   Setup complete.
echo   IMPORTANT: CLOSE this window, then
echo   double-click  run-dev.bat  to start the app.
echo   (A new window is needed for the Java PATH.)
echo ============================================
pause
