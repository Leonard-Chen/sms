@echo off
chcp 65001 >nul
call "%~dp0_root.bat"
echo [%SMS_ROOT%] sms-system 端口 9100 ^| 需 MySQL/Redis/Nacos
pause
mvn -pl sms-system -am spring-boot:run
