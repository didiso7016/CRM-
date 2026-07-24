@echo off
setlocal
cd /d "%~dp0"

echo ============================================
echo   CRM Quotation System - Starting
echo ============================================

REM ---- Pick Java: prefer bundled jre, else system java ----
set "JAVA_EXE="
if exist "%~dp0jre\bin\java.exe" (
    set "JAVA_EXE=%~dp0jre\bin\java.exe"
) else (
    where java >nul 2>nul && set "JAVA_EXE=java"
)
if "%JAVA_EXE%"=="" (
    echo [ERROR] Java not found. Make sure the jre folder exists next to this file.
    pause
    exit /b 1
)

REM ---- Locate the jar ----
set "JAR=%~dp0crm.jar"
if not exist "%JAR%" (
    for %%f in ("%~dp0*.jar") do set "JAR=%%f"
)
if not exist "%JAR%" (
    echo [ERROR] Program jar not found. Run build-portable.bat first.
    pause
    exit /b 1
)

REM ---- Start server in its own window (close it to stop the system) ----
echo Starting server...
start "CRM Server - do not close" "%JAVA_EXE%" -jar "%JAR%"

REM ---- Wait until ready, then open the browser ----
echo Waiting for the server (about 5-15 seconds)...
powershell -NoProfile -Command "for($i=0;$i -lt 40;$i++){try{Invoke-WebRequest 'http://localhost:8080/' -UseBasicParsing -TimeoutSec 2 | Out-Null; exit 0}catch{Start-Sleep -Seconds 1}}; exit 1"
if errorlevel 1 (
    echo [NOTE] Server not responding yet. Please open the browser manually.
) else (
    start "" "http://localhost:8080"
    echo Opened browser at: http://localhost:8080
)

echo.
echo To stop the system, close the window titled "CRM Server".
echo.
pause
