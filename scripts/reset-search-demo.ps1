param(
    [int]$ReindexPort = 18081,
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$backendDir = Join-Path $repoRoot "backend"
$jarPath = Join-Path $backendDir "target\product-service-0.0.1-SNAPSHOT.jar"
$appProcess = $null

function Wait-HttpOk {
    param(
        [string]$Url,
        [int]$TimeoutSeconds = 90
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            $code = curl.exe -s -o NUL -w "%{http_code}" $Url
            if ($code -eq "200" -or $code -eq "302") {
                return
            }
        } catch {
            Start-Sleep -Seconds 1
        }
        Start-Sleep -Seconds 2
    }
    throw "Timed out waiting for $Url"
}

function Stop-ListeningProcess {
    param([int]$Port)

    $listeners = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    foreach ($listener in $listeners) {
        Stop-Process -Id $listener.OwningProcess -Force -ErrorAction SilentlyContinue
    }
}

try {
    Push-Location $repoRoot

    if (-not $SkipBuild) {
        docker compose build elasticsearch
    }

    docker compose up -d mysql elasticsearch kibana

    Write-Host "Waiting for Elasticsearch..."
    Wait-HttpOk "http://localhost:9200" 120

    Write-Host "Waiting for MySQL..."
    $mysqlReady = $false
    for ($i = 0; $i -lt 60; $i++) {
        docker exec product-mysql mysqladmin ping -uroot -p123456 --silent | Out-Null
        if ($LASTEXITCODE -eq 0) {
            $mysqlReady = $true
            break
        }
        Start-Sleep -Seconds 2
    }
    if (-not $mysqlReady) {
        throw "Timed out waiting for MySQL"
    }

    Write-Host "Recreating MySQL schema and seed data..."
    cmd /c "docker exec -i product-mysql mysql -uroot -p123456 --default-character-set=utf8mb4 < backend\src\main\resources\sql\schema.sql"
    cmd /c "docker exec -i product-mysql mysql -uroot -p123456 --default-character-set=utf8mb4 < backend\src\main\resources\sql\seed_data.sql"

    Write-Host "Building backend..."
    Push-Location $backendDir
    mvn -q -DskipTests package
    Pop-Location

    Write-Host "Starting temporary backend on port $ReindexPort..."
    $existingListener = Get-NetTCPConnection -LocalPort $ReindexPort -State Listen -ErrorAction SilentlyContinue
    if ($existingListener) {
        throw "Port $ReindexPort is already in use. Pass -ReindexPort with a free port."
    }
    $appProcess = Start-Process -FilePath java `
        -ArgumentList @("-jar", $jarPath, "--server.port=$ReindexPort") `
        -WorkingDirectory $backendDir `
        -PassThru `
        -WindowStyle Hidden

    Wait-HttpOk "http://localhost:$ReindexPort/swagger-ui.html" 120

    Write-Host "Recreating Elasticsearch index from MySQL..."
    $result = Invoke-RestMethod `
        -Uri "http://localhost:$ReindexPort/api/admin/search/reindex" `
        -Method Post `
        -Headers @{ "X-Role" = "OPERATOR" }
    $result | ConvertTo-Json -Depth 8

    Write-Host "Search demo reset complete."
} finally {
    if ($appProcess -and -not $appProcess.HasExited) {
        Stop-Process -Id $appProcess.Id -Force
    }
    Stop-ListeningProcess $ReindexPort
    Pop-Location
}
