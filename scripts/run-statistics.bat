@echo off
chcp 65001 >nul
call "%~dp0_root.bat"
echo [%SMS_ROOT%] sms-statistics 端口 9500
pause
mvn -pl sms-statistics -am spring-boot:run
