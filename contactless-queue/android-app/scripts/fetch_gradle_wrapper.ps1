# Downloads and extracts the Gradle wrapper jar from the Gradle distribution
param(
    [string]$GradleVersion = "7.5.1"
)

$distUrl = "https://services.gradle.org/distributions/gradle-$GradleVersion-bin.zip"
$tmp = Join-Path $env:TEMP "gradle-wrapper-$GradleVersion.zip"
$targetDir = Join-Path $PSScriptRoot "..\gradle\wrapper"
if (-not (Test-Path $targetDir)) { New-Item -ItemType Directory -Path $targetDir -Force | Out-Null }

Write-Output "Downloading $distUrl to $tmp"
Invoke-WebRequest -Uri $distUrl -OutFile $tmp

Write-Output "Extracting gradle-wrapper.jar"
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory($tmp, $env:TEMP + "\gradle-unzip")

$jarSource = Join-Path $env:TEMP "gradle-unzip\gradle-$GradleVersion\lib\gradle-wrapper.jar"
$dest = Join-Path $targetDir "gradle-wrapper.jar"
Copy-Item -Path $jarSource -Destination $dest -Force

Write-Output "gradle-wrapper.jar saved to: $dest"
