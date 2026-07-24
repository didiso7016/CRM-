@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo ============================================
echo   Build portable package (jar + slim Java)
echo ============================================

REM ---- Find JAVA_HOME (needs jmods for jlink) ----
if "%JAVA_HOME%"=="" (
    for /d %%d in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do set "JAVA_HOME=%%d"
)
if "%JAVA_HOME%"=="" (
    echo [ERROR] JAVA_HOME not found. Install JDK 17 or set JAVA_HOME.
    pause
    exit /b 1
)
echo Using JDK: %JAVA_HOME%

REM ---- 1. Build the jar (runs tests) ----
echo.
echo [1/4] Compiling and packaging...
call "%~dp0mvnw.cmd" -q clean package
if errorlevel 1 (
    echo [ERROR] Build failed.
    pause
    exit /b 1
)

REM ---- 2. Prepare dist folder (keep data/backup/uploads so user data is NOT wiped) ----
echo [2/4] Preparing dist folder...
if not exist dist mkdir dist
if exist "dist\crm.jar" del /q "dist\crm.jar"
for %%f in (target\crm-*.jar) do copy /y "%%f" "dist\crm.jar" >nul

REM ---- 3. jlink slim JRE ----
echo [3/4] Building slim Java runtime (jre)...
if exist "dist\jre" rmdir /s /q "dist\jre"
"%JAVA_HOME%\bin\jlink" --module-path "%JAVA_HOME%\jmods" --add-modules java.se,jdk.localedata,jdk.charsets,jdk.crypto.ec,jdk.crypto.cryptoki,jdk.unsupported --include-locales=zh-TW,en-US --no-header-files --no-man-pages --strip-debug --compress=2 --output "dist\jre"
if errorlevel 1 (
    echo [ERROR] jlink failed.
    pause
    exit /b 1
)

REM ---- 4. Copy launcher and backup scripts ----
echo [4/4] Copying launcher and backup scripts...
copy /y start-crm.bat     dist\ >nul
copy /y backup-crm.bat    dist\ >nul
copy /y backup.bat        dist\ >nul
copy /y restore-guide.txt dist\ >nul

echo.
echo ============================================
echo   Done. Portable package is in the dist folder.
echo   Copy the whole dist folder to any Windows PC
echo   and run start-crm.bat (no Java install needed).
echo ============================================
pause
