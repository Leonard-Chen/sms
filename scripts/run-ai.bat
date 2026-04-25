@echo off
chcp 65001 >nul
call "%~dp0_root.bat"
echo [%SMS_ROOT%] sms-ai 端口 9600
pause
mvn -pl sms-ai -am spring-boot:run
