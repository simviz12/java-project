@echo off
title ProductividadPlus Docker Launcher

rem Verify Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Docker Desktop is not running. Please start Docker and try again.
    pause
    exit /b 1
)

rem Pull MongoDB image and build the Spring Boot image
echo ^>^> Pulling MongoDB image…
docker compose pull mongo

echo ^>^> Building Spring Boot image…
docker compose build --no-cache

rem Start containers in detached mode
echo ^>^> Starting containers (detached)…
docker compose up -d

rem Wait a few seconds for MongoDB to be ready
timeout /t 5 /nobreak >nul

rem Open the web UI
start http://localhost:8080

echo.
echo ✅ All services are up. Use "docker compose down" to stop them.
pause
