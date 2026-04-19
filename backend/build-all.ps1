# build-all.ps1
$ErrorActionPreference = "Stop"

# 1. Setup Maven
$MavenPath = "$env:USERPROFILE\Documents\maven\apache-maven-3.9.6\bin"
if (Test-Path $MavenPath) {
    $env:Path = "$MavenPath;$env:Path"
    Write-Host "Maven Path Configured" -ForegroundColor Cyan
}
else {
    Write-Warning "Maven path not found at $MavenPath. Assuming it's in system PATH."
}

Write-Host "========= CLEANING & BUILDING ALL SERVICES =========" -ForegroundColor Cyan

# 2. Build Root (Parent) to install common deps
Write-Host "Building Root & Common..." -ForegroundColor Yellow
mvn clean install -DskipTests
if ($LASTEXITCODE -ne 0) { throw "Build failed" }

Write-Host "Build Complete! JARs are ready." -ForegroundColor Green
