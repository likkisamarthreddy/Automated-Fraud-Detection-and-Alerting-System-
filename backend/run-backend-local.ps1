# run-backend-local.ps1
# Script to run FraudGuard backend services locally without Docker

# Database settings are now managed in each service's application.yml (port 3307)
$Env:SPRING_DATASOURCE_USERNAME = "frauduser"
$Env:SPRING_DATASOURCE_PASSWORD = "fraudpass"
$Env:SPRING_DATA_REDIS_HOST = "localhost"
$Env:JWT_SECRET = "Z3J1cG8tZnJhdWQtZGV0ZWN0aW9uLXN5c3RlbS1zZWNyZXQta2V5LTIwMjQ="
$Env:SPRING_KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"

$MavenPath = "$env:USERPROFILE\Documents\maven\apache-maven-3.9.6\bin"
if (Test-Path $MavenPath) {
    $env:Path = "$MavenPath;$env:Path"
    Write-Host "Added Maven to Path: $MavenPath" -ForegroundColor Cyan
}

Write-Host "Starting FraudGuard Backend Services Locally..." -ForegroundColor Cyan


# build common first
Write-Host "Building common module..." -ForegroundColor Yellow
cd common
mvn clean install -DskipTests
cd ..

$services = @(
    @{ name = "api-gateway"; port = 8080; path = "api-gateway" },
    @{ name = "auth-service"; port = 8081; path = "auth-service" },
    @{ name = "transaction-service"; port = 8082; path = "transaction-service" },
    @{ name = "fraud-engine-service"; port = 8083; path = "fraud-engine-service" },
    @{ name = "alert-service"; port = 8084; path = "alert-service" }
)

foreach ($svc in $services) {
    Write-Host "Starting $($svc.name) on port $($svc.port)..." -ForegroundColor Green
    $cmd = "`$env:Path = '$MavenPath;' + `$env:Path; cd $($svc.path); mvn spring-boot:run"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $cmd
}

Write-Host "All services triggered. Check individual windows for logs." -ForegroundColor Cyan
