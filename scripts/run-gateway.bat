@echo off
chcp 65001 >nul
call "%~dp0_root.bat"
echo [%SMS_ROOT%] sms-gateway 端口 9000 ^| 建议先启动 Nacos 与其它业务服务
pause
mvn -pl sms-gateway -am spring-boot:run
