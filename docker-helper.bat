@echo off
REM Docker Compose helper script

echo ========================================
echo   MinIO Data Lake - Docker Compose
echo ========================================
echo.

REM Check if Docker is installed
where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Docker is not installed or not in PATH
    echo Please install Docker Desktop from: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

echo Available commands:
echo   1. Start MinIO
echo   2. Stop MinIO
echo   3. View logs
echo   4. Clean up
echo   5. Exit
echo.

set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto start
if "%choice%"=="2" goto stop
if "%choice%"=="3" goto logs
if "%choice%"=="4" goto cleanup
if "%choice%"=="5" goto end

:start
echo.
echo Starting MinIO with Docker Compose...
docker-compose up -d
echo.
echo [OK] MinIO started successfully!
echo.
echo Access MinIO:
echo   - API: http://localhost:9000
echo   - Console: http://localhost:9001
echo   - Username: minioadmin
echo   - Password: minioadmin123
echo.
pause
goto end

:stop
echo.
echo Stopping MinIO...
docker-compose stop
echo [OK] MinIO stopped
pause
goto end

:logs
echo.
echo Viewing MinIO logs (Ctrl+C to exit)...
docker-compose logs -f minio
goto end

:cleanup
echo.
echo WARNING: This will remove MinIO container and data!
set /p confirm="Are you sure? (yes/no): "
if /i "%confirm%"=="yes" (
    docker-compose down -v
    echo [OK] Cleanup complete
) else (
    echo Cleanup cancelled
)
pause
goto end

:end

