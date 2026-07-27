@echo off
REM Development launcher: run the app straight from source code.
REM Use this while you are still editing the program (no packaging needed).
cd /d "%~dp0"

echo ============================================
echo   CRM - Development mode (running from source)
echo ============================================
echo Open your browser at: http://localhost:8080
echo Press Ctrl+C in this window to stop.
echo.

call "%~dp0mvnw.cmd" spring-boot:run
pause
