@echo off
chcp 65001 >nul
call "%~dp0_root.bat"
echo [%SMS_ROOT%] sms-customer 端口 9200
pause
mvn -pl sms-customer -am spring-boot:run
