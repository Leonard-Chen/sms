@echo off
rem 切换到仓库根目录（本文件位于 scripts 下）
for %%I in ("%~dp0..") do set "SMS_ROOT=%%~fI"
if not exist "%SMS_ROOT%" (
    echo 错误：无法定位仓库根目录！
    pause
    exit /b 1
)
cd /d "%SMS_ROOT%" || (
    echo 错误：切换到仓库根目录失败！路径：%SMS_ROOT%
    pause
    exit /b 1
)
