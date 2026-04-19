@echo off
chcp 65001 >nul
call "%~dp0_root.bat"
echo [%SMS_ROOT%] sms-employee 端口 9400
pause
mvn -pl sms-employee -am spring-boot:run
