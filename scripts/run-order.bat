@echo off
chcp 65001 >nul
call "%~dp0_root.bat"
echo [%SMS_ROOT%] sms-order 端口 9300
pause
mvn -pl sms-order -am spring-boot:run
