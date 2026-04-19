# run-demo-stable.ps1
$ErrorActionPreference = "Continue" # Don't stop on minor errors, we handle them

# Clear potential persistent env vars from previous runs
Remove-Item Env:\SPRING_DATASOURCE_URL -ErrorAction SilentlyContinue

# 1. Environment Config (Credentials & Shared Infra)
# Note: SPRING_DATASOURCE_URL is NOT set here. Each service uses its own application.yml (port 3307).
$Env:SPRING_DATASOURCE_USERNAME = "frauduser"
$Env:SPRING_DATASOURCE_PASSWORD = "fraudpass"
$Env:SPRING_DATA_REDIS_HOST = "localhost"
$Env:JWT_SECRET = "Z3J1cG8tZnJhdWQtZGV0ZWN0aW9uLXN5c3RlbS1zZWNyZXQta2V5LTIwMjQ="
$Env:SPRING_KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"

# 2. Start Services (JARs)
function Start-ServiceJAR ($name, $path, $port) {
    Write-Host "Starting $name on port $port..." -ForegroundColor Cyan
    $jar = Get-ChildItem "$path\target\*.jar" | Select-Object -First 1
    if (!$jar) { Write-Error "JAR not found for $name. Did build succeed?"; return }
    
    # Run in background with logging
    $logOut = "$path\service.log"
    $logErr = "$path\service_err.log"
    Start-Process java -ArgumentList "-jar", $jar.FullName, "--server.port=$port" -RedirectStandardOutput $logOut -RedirectStandardError $logErr -WindowStyle Minimized
}

Write-Host "========= STARTING BACKEND (JAR MODE) =========" -ForegroundColor Cyan

Start-ServiceJAR "API Gateway" "api-gateway" 8080
Start-ServiceJAR "Auth Service" "auth-service" 8081
Start-ServiceJAR "Transaction Service" "transaction-service" 8082
Start-ServiceJAR "Fraud Engine" "fraud-engine-service" 8083
Start-ServiceJAR "Alert Service" "alert-service" 8084

# 3. Start Frontend
Write-Host "Starting Frontend..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd ..\fraudguard-console-main; npm run dev" -WindowStyle Minimized

Write-Host "`nWaiting 30s for services to warm up..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# 4. Run Demo Generator
Write-Host "`nExecuting Real Transaction Setup..." -ForegroundColor Cyan
cd .. # Go back to root
.\demo-setup.ps1
