@echo off

if not exist "%~dp0_root.bat" (
    echo 错误：缺失 _root.bat，请确认文件是否存在！
    pause
    exit /b 1
)
call "%~dp0_root.bat"

set "R=%SMS_ROOT%"
echo ==============================================
echo 项目根目录: %R%
echo 要求：已先启动 MySQL(sms)、Redis、Nacos(8848)
echo ==============================================
pause>nul

start "sms-system" cmd /k ""cd /d "%R%" && title sms-system && mvn -pl sms-system -am spring-boot:run""
echo 等待 sms-system 注册到 Nacos（约 35 秒，按 Ctrl+C 可中断等待手动启动后续服务）...
timeout /t 35 >nul 2>&1

start "sms-ai" cmd /k ""cd /d "%R%" && title sms-ai && mvn -pl sms-ai -am spring-boot:run""
start "sms-customer" cmd /k ""cd /d "%R%" && title sms-customer && mvn -pl sms-customer -am spring-boot:run""
start "sms-order" cmd /k ""cd /d "%R%" && title sms-order && mvn -pl sms-order -am spring-boot:run""
start "sms-employee" cmd /k ""cd /d "%R%" && title sms-employee && mvn -pl sms-employee -am spring-boot:run""
start "sms-statistics" cmd /k ""cd /d "%R%" && title sms-statistics && mvn -pl sms-statistics -am spring-boot:run""
echo 等待业务服务启动（约 20 秒，按 Ctrl+C 可中断等待）...
timeout /t 20 >nul 2>&1

start "sms-gateway" cmd /k ""cd /d "%R%" && title sms-gateway && mvn -pl sms-gateway -am spring-boot:run""
echo ==============================================
echo 已尝试启动全部窗口！
echo 网关访问地址：http://localhost:9000
echo 若部分服务启动失败，请检查：
echo  1. JAVA_HOME 配置为JDK 21根目录（非/bin）
echo  2. MySQL/Redis/Nacos 已正常启动
echo  3. 各服务端口未被占用
echo ==============================================
pause
