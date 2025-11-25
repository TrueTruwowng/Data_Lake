@echo off
REM Deploy MinIO to Kubernetes

echo ========================================
echo   Deploy MinIO to Kubernetes
echo ========================================
echo.

REM Check if kubectl is installed
where kubectl > nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] kubectl not found
    echo Please install kubectl first
    pause
    exit /b 1
)

echo Checking Kubernetes connection...
kubectl cluster-info > nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Cannot connect to Kubernetes cluster
    echo Please ensure your cluster is running and kubectl is configured
    pause
    exit /b 1
)

echo [OK] Connected to Kubernetes cluster
echo.

echo Deploying MinIO...
kubectl apply -f k8s\minio-deployment.yaml

if %errorlevel% neq 0 (
    echo [ERROR] Deployment failed
    pause
    exit /b 1
)

echo.
echo [OK] MinIO deployed successfully
echo.

echo Waiting for pods to be ready...
kubectl wait --for=condition=ready pod -l app=minio -n data-lake --timeout=300s

echo.
echo Checking deployment status...
kubectl get pods -n data-lake
kubectl get svc -n data-lake

echo.
echo ========================================
echo   MinIO Deployment Complete
echo ========================================
echo.
echo To access MinIO, run:
echo   kubectl port-forward -n data-lake svc/minio-service 9000:9000 9001:9001
echo.
echo Then access:
echo   - API: http://localhost:9000
echo   - Console: http://localhost:9001
echo   - Username: minioadmin
echo   - Password: minioadmin123
echo.
pause

