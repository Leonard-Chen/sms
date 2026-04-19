# 需「以管理员身份运行 PowerShell」执行。
# 将 JDK 21 的 bin 放到「系统 Path」最前面，使新开 cmd 里直接输入 java 即为 21（与 1.8 共存时仍保留 8 的路径，只是优先级靠后）。
$ErrorActionPreference = 'Stop'
$jdk21Bin = 'F:\dev\jdk-21.0.10+7\bin'
if (-not (Test-Path (Join-Path $jdk21Bin 'java.exe'))) {
    Write-Error "未找到 $jdk21Bin\java.exe，请先解压 JDK 到 F:\dev"
}
$isAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Error '请右键 PowerShell -> 以管理员身份运行，再执行本脚本。'
}
$mPath = [Environment]::GetEnvironmentVariable('Path', 'Machine')
$parts = $mPath -split ';' | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne '' -and $_ -ne $jdk21Bin }
$newMachinePath = ($jdk21Bin + ';' + ($parts -join ';'))
[Environment]::SetEnvironmentVariable('Path', $newMachinePath, 'Machine')
Write-Host '已把 JDK 21 bin 置于系统 Path 最前。请重新打开终端后执行 java -version 验证。'
