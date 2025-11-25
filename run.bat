@echo off
REM Script to run Data Lake application

echo ========================================
echo   Data Lake Application Launcher
echo ========================================
echo.

REM Check if MinIO is running
echo Checking MinIO connection...
curl -s http://localhost:9000/minio/health/live > nul 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] MinIO is not running on localhost:9000
    echo.
    echo Please start MinIO first:
    echo   Option 1: docker run -p 9000:9000 -p 9001:9001 -e "MINIO_ROOT_USER=minioadmin" -e "MINIO_ROOT_PASSWORD=minioadmin123" quay.io/minio/minio server /data --console-address ":9001"
    echo   Option 2: minio.exe server E:\data --console-address ":9001"
    echo.
    pause
    exit /b 1
)

echo [OK] MinIO is running
echo.

echo Building project...
call mvn clean compile
if %errorlevel% neq 0 (
    echo [ERROR] Build failed
    pause
    exit /b 1
)

echo.
echo Running application...
echo.
call mvn exec:java -Dexec.mainClass="com.example.Main"

echo.
echo ========================================
echo   Application finished
echo ========================================
pause

