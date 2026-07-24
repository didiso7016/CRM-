@echo off
setlocal
cd /d "%~dp0"

echo ============================================
echo   CRM Database Backup
echo ============================================

if not exist "data\crm.db" (
    echo [ERROR] data\crm.db not found. Nothing to back up.
    pause
    exit /b 1
)

if not exist "backup" mkdir "backup"

REM ---- Timestamped filename: yyyyMMdd-HHmmss ----
for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd-HHmmss"') do set "TS=%%i"
set "TARGET=backup\crm-backup-%TS%.db"

copy /y "data\crm.db" "%TARGET%" >nul
if errorlevel 1 (
    echo [ERROR] Backup failed.
    pause
    exit /b 1
)
echo Backup created: %TARGET%

REM ---- Keep only the most recent 30 backups ----
powershell -NoProfile -Command "Get-ChildItem 'backup' -Filter 'crm-backup-*.db' | Sort-Object Name -Descending | Select-Object -Skip 30 | Remove-Item -Force"

echo Done. (Only the latest 30 backups are kept.)
pause
