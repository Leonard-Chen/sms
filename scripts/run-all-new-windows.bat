@echo off
chcp 65001 >nul
call "%~dp0_root.bat"
set "R=%SMS_ROOT%"
echo 仓库: %R%
echo 请先启动 MySQL(sms)、Redis、Nacos(8848)，再按任意键打开多个服务窗口...
pause>nul

start "sms-system" cmd /k "cd /d "%R%" && title sms-system && mvn -pl sms-system -am spring-boot:run"
echo 等待 system 注册到 Nacos（约 35 秒，可按需 Ctrl+C 中断等待后手动启后续）...
timeout /t 35 /nobreak >nul

start "sms-customer" cmd /k "cd /d "%R%" && title sms-customer && mvn -pl sms-customer -am spring-boot:run"
start "sms-order" cmd /k "cd /d "%R%" && title sms-order && mvn -pl sms-order -am spring-boot:run"
start "sms-employee" cmd /k "cd /d "%R%" && title sms-employee && mvn -pl sms-employee -am spring-boot:run"
start "sms-statistics" cmd /k "cd /d "%R%" && title sms-statistics && mvn -pl sms-statistics -am spring-boot:run"
timeout /t 20 /nobreak >nul

start "sms-gateway" cmd /k "cd /d "%R%" && title sms-gateway && mvn -pl sms-gateway -am spring-boot:run"
echo 已尝试启动全部窗口。网关: http://localhost:9000
pause
