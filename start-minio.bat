@echo off
REM Start MinIO server locally for development

echo ========================================
echo   Starting MinIO Server
echo ========================================
echo.

set MINIO_ROOT_USER=minioadmin
set MINIO_ROOT_PASSWORD=minioadmin123

REM Create data directory if not exists
if not exist "E:\minio-data" mkdir "E:\minio-data"

echo MinIO Configuration:
echo   Endpoint: http://localhost:9000
echo   Console: http://localhost:9001
echo   Username: minioadmin
echo   Password: minioadmin123
echo   Data Directory: E:\minio-data
echo.

REM Check if minio.exe exists
where minio.exe > nul 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] minio.exe not found in PATH
    echo.
    echo Please download MinIO:
    echo   1. Visit: https://min.io/download
    echo   2. Download Windows binary
    echo   3. Place minio.exe in this directory or add to PATH
    echo.
    echo Alternative: Use Docker
    echo   docker run -p 9000:9000 -p 9001:9001 -e "MINIO_ROOT_USER=minioadmin" -e "MINIO_ROOT_PASSWORD=minioadmin123" quay.io/minio/minio server /data --console-address ":9001"
    pause
    exit /b 1
)

echo Starting MinIO server...
echo Press Ctrl+C to stop
echo.

minio.exe server E:\minio-data --console-address ":9001"

