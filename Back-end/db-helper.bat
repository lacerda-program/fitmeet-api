@echo off
REM Database Helper Script for Windows

if "%~1"=="" (
    echo Usage: db-helper.bat [command]
    echo.
    echo Commands:
    echo   start       - Start PostgreSQL and LocalStack containers
    echo   stop        - Stop all containers
    echo   logs        - Show PostgreSQL logs
    echo   status      - Check container status
    echo   clean       - Stop and remove all containers and volumes
    echo   restart     - Restart all containers
    echo   psql        - Connect to database with psql
    exit /b 0
)

if "%~1"=="start" (
    echo Starting Docker containers...
    docker-compose up -d
    echo.
    echo Waiting for containers to be ready...
    timeout /t 5 /nobreak
    docker-compose ps
    goto :eof
)

if "%~1"=="stop" (
    echo Stopping Docker containers...
    docker-compose down
    goto :eof
)

if "%~1"=="logs" (
    echo PostgreSQL logs:
    docker-compose logs postgres
    goto :eof
)

if "%~1"=="status" (
    echo Container Status:
    docker-compose ps
    echo.
    echo Container Health:
    docker inspect bootcamp_postgres --format="{{.State.Health.Status}}"
    goto :eof
)

if "%~1"=="clean" (
    echo WARNING: This will remove all containers and volumes including data!
    set /p confirm="Are you sure? (yes/no): "
    if /i "%confirm%"=="yes" (
        docker-compose down -v
        echo Cleanup complete.
    ) else (
        echo Cleanup cancelled.
    )
    goto :eof
)

if "%~1"=="restart" (
    echo Restarting Docker containers...
    docker-compose down
    timeout /t 3 /nobreak
    docker-compose up -d
    echo.
    docker-compose ps
    goto :eof
)

if "%~1"=="psql" (
    echo Connecting to PostgreSQL database...
    docker exec -it bootcamp_postgres psql -U postgres -d bootcamp_db
    goto :eof
)

echo Unknown command: %~1
echo Run: db-helper.bat [no args] for usage information

