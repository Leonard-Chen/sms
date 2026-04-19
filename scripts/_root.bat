@echo off
rem 切换到仓库根目录（本文件位于 scripts 下）
for %%I in ("%~dp0..") do set "SMS_ROOT=%%~fI"
cd /d "%SMS_ROOT%"
