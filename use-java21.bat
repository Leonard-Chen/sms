@echo off
REM 当前窗口临时使用 JDK 21（优先于系统 Path 里的 Oracle javapath）
set "JAVA_HOME=F:\dev\jdk-21.0.10+7"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo JAVA_HOME=%JAVA_HOME%
java -version
