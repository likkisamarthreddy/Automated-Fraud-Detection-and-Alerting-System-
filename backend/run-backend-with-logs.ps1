# run-backend-with-logs.ps1
$Env:SPRING_DATASOURCE_USERNAME = "frauduser"
$Env:SPRING_DATASOURCE_PASSWORD = "fraudpass"
$Env:SPRING_DATA_REDIS_HOST = "localhost"
$Env:JWT_SECRET = "Z3J1cG8tZnJhdWQtZGV0ZWN0aW9uLXN5c3RlbS1zZWNyZXQta2V5LTIwMjQ="
$Env:SPRING_KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"

$MavenPath = "$env:USERPROFILE\Documents\maven\apache-maven-3.9.6\bin"
if (Test-Path $MavenPath) {
    $env:Path = "$MavenPath;$env:Path"
}

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
    $logFile = "..\$($svc.name).log"
    $cmd = "`$env:Path = '$MavenPath;' + `$env:Path; cd $($svc.path); mvn spring-boot:run > $logFile 2>&1"
    Start-Process powershell -WindowStyle Hidden -ArgumentList "-Command", $cmd
}
