@echo off
REM backup.bat: same as backup-crm.bat, delegates to the main backup script.
call "%~dp0backup-crm.bat"
