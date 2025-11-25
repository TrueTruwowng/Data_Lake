# Hướng dẫn Cài đặt và Chạy Data Lake

## 🎯 Mục tiêu
Cài đặt hệ thống Data Lake sử dụng MinIO (Object Storage) và DuckDB (Query Engine) để:
- Lưu trữ dữ liệu CSV trên MinIO
- Truy vấn dữ liệu CSV sử dụng DuckDB
- Triển khai MinIO trên Kubernetes với 3 replicas

## 📋 Yêu cầu hệ thống

### Phần mềm cần thiết:
- ✅ Java 21 hoặc cao hơn
- ✅ Maven 3.6+
- ✅ Docker Desktop (khuyến nghị) HOẶC MinIO binary
- ✅ Kubernetes cluster (cho production deployment)
- ✅ kubectl (cho K8s deployment)

### Kiểm tra:
```cmd
java -version
mvn -version
docker --version
kubectl version --client
```

## 🚀 Phương án 1: Quick Start (Docker - Khuyến nghị)

### Bước 1: Clone/Download project
```cmd
cd E:\StudyDoc\NAM3\PTUDDN\Data_Lake
```

### Bước 2: Start MinIO bằng Docker Compose
```cmd
docker-compose up -d
```

Hoặc sử dụng script helper:
```cmd
docker-helper.bat
:: Chọn option 1 (Start MinIO)
```

### Bước 3: Verify MinIO đang chạy
```cmd
docker ps
```

Bạn sẽ thấy container `minio-datalake` đang chạy.

Mở browser: http://localhost:9001
- Username: `minioadmin`
- Password: `minioadmin123`

### Bước 4: Build Java project
```cmd
mvn clean install
```

### Bước 5: Run application
```cmd
mvn exec:java -Dexec.mainClass="com.example.Main"
```

Hoặc sử dụng script:
```cmd
run.bat
```

### Kết quả mong đợi:
```
=================================================
   Data Lake Application - MinIO + DuckDB
=================================================

--- DEMO 1: Upload CSV Data to MinIO ---
✓ Uploaded sales_data.csv
✓ Uploaded products_data.csv
✓ Uploaded employees_data.csv (100 records)

--- DEMO 2: List CSV Files in MinIO ---
CSV files in MinIO Data Lake:
  - sales/sales_data.csv
  - products/products_data.csv
  - employees/employees_data.csv
Total files: 3

--- DEMO 3: Query CSV Data with DuckDB ---
...
```

## 🏢 Phương án 2: Production Deployment (Kubernetes)

### Bước 1: Verify Kubernetes cluster
```cmd
kubectl cluster-info
kubectl get nodes
```

### Bước 2: Deploy MinIO lên Kubernetes
```cmd
kubectl apply -f k8s\minio-deployment.yaml
```

Hoặc sử dụng script:
```cmd
deploy-k8s.bat
```

### Bước 3: Kiểm tra deployment
```cmd
:: Xem pods
kubectl get pods -n data-lake

:: Xem services
kubectl get svc -n data-lake

:: Xem persistent volumes
kubectl get pvc -n data-lake

:: Xem logs
kubectl logs -n data-lake minio-0
```

### Bước 4: Truy cập MinIO từ local machine
```cmd
kubectl port-forward -n data-lake svc/minio-service 9000:9000 9001:9001
```

Giữ terminal này mở, mở terminal mới để tiếp tục.

### Bước 5: Cập nhật code để connect tới K8s
Mở file `src/main/java/com/example/Main.java`, tìm dòng:
```java
MinioConfig config = MinioConfig.getDefaultConfig();
```

Thay đổi thành:
```java
MinioConfig config = MinioConfig.getK8sConfig();
```

**LƯU Ý:** Nếu đang port-forward từ localhost, giữ nguyên `getDefaultConfig()`.

### Bước 6: Run application
```cmd
mvn exec:java -Dexec.mainClass="com.example.Main"
```

## 🔧 Phương án 3: MinIO Binary (Không dùng Docker)

### Bước 1: Download MinIO
1. Truy cập: https://min.io/download
2. Download Windows binary
3. Đổi tên thành `minio.exe`
4. Đặt vào thư mục project hoặc thêm vào PATH

### Bước 2: Start MinIO
```cmd
start-minio.bat
```

Hoặc chạy trực tiếp:
```cmd
set MINIO_ROOT_USER=minioadmin
set MINIO_ROOT_PASSWORD=minioadmin123
minio.exe server E:\minio-data --console-address ":9001"
```

### Bước 3: Build và Run application
```cmd
mvn clean install
mvn exec:java -Dexec.mainClass="com.example.Main"
```

## 📊 Kiểm tra kết quả

### 1. Trong MinIO Console
1. Mở browser: http://localhost:9001
2. Login với `minioadmin` / `minioadmin123`
3. Vào Buckets → `data-lake`
4. Bạn sẽ thấy các thư mục:
   - `sales/`
   - `products/`
   - `employees/`
   - `analytics/`

### 2. Trong Console Output
Application sẽ hiển thị:
- Upload status
- File listing
- Query results
- Analytics output

### 3. Verify data
```cmd
:: List files trong MinIO
curl http://localhost:9000/data-lake/
```

## 🛠️ Troubleshooting

### Lỗi 1: "Connection refused" khi kết nối MinIO
**Nguyên nhân:** MinIO chưa start hoặc port bị chiếm

**Giải pháp:**
```cmd
:: Check MinIO đang chạy
docker ps
netstat -an | findstr 9000

:: Restart MinIO
docker-compose restart
```

### Lỗi 2: "Cannot resolve symbol 'io'" trong IDE
**Nguyên nhân:** Dependencies chưa được download

**Giải pháp:**
```cmd
:: Force update dependencies
mvn clean install -U

:: Trong IntelliJ IDEA:
:: File → Invalidate Caches → Invalidate and Restart
```

### Lỗi 3: "Out of Memory" khi chạy application
**Nguyên nhân:** Java heap size không đủ

**Giải pháp:**
```cmd
set MAVEN_OPTS=-Xmx2g
mvn exec:java -Dexec.mainClass="com.example.Main"
```

### Lỗi 4: Kubernetes pods không start
**Nguyên nhân:** Resources không đủ hoặc image pull failed

**Giải pháp:**
```cmd
:: Check pod status
kubectl describe pod -n data-lake minio-0

:: Check events
kubectl get events -n data-lake

:: Reduce resource requests trong minio-deployment.yaml
```

### Lỗi 5: Port 9000/9001 already in use
**Nguyên nhân:** Có service khác đang dùng port

**Giải pháp:**
```cmd
:: Tìm process đang dùng port
netstat -ano | findstr 9000

:: Kill process (thay PID)
taskkill /PID <PID> /F

:: Hoặc thay đổi port trong docker-compose.yml
```

## 📝 Các lệnh hữu ích

### Docker Commands
```cmd
:: Start MinIO
docker-compose up -d

:: Stop MinIO
docker-compose stop

:: View logs
docker-compose logs -f

:: Restart MinIO
docker-compose restart

:: Remove all (including data)
docker-compose down -v
```

### Kubernetes Commands
```cmd
:: Deploy
kubectl apply -f k8s\minio-deployment.yaml

:: Get pods
kubectl get pods -n data-lake

:: Get services
kubectl get svc -n data-lake

:: Logs
kubectl logs -n data-lake minio-0

:: Shell into pod
kubectl exec -it -n data-lake minio-0 -- /bin/sh

:: Port forward
kubectl port-forward -n data-lake svc/minio-service 9000:9000

:: Delete deployment
kubectl delete -f k8s\minio-deployment.yaml
```

### Maven Commands
```cmd
:: Clean and compile
mvn clean compile

:: Run application
mvn exec:java -Dexec.mainClass="com.example.Main"

:: Package JAR
mvn clean package

:: Run JAR
java -jar target\Data_Lake-1.0-SNAPSHOT.jar

:: Skip tests
mvn clean install -DskipTests

:: Update dependencies
mvn clean install -U
```

### MinIO CLI (mc) Commands
```cmd
:: Install mc (MinIO Client)
:: Download from: https://min.io/download#/windows

:: Configure alias
mc alias set myminio http://localhost:9000 minioadmin minioadmin123

:: List buckets
mc ls myminio

:: List files in bucket
mc ls myminio/data-lake

:: Copy file
mc cp myfile.csv myminio/data-lake/

:: Remove file
mc rm myminio/data-lake/myfile.csv
```

## 🎓 Next Steps

### 1. Tùy chỉnh ứng dụng
- Modify `Main.java` để test các use cases khác
- Tạo CSV files riêng
- Viết custom queries

### 2. Tích hợp với hệ thống khác
- REST API endpoint
- Scheduled jobs
- Kafka integration
- Spark processing

### 3. Production-ready
- Enable TLS/SSL
- Change default credentials
- Setup monitoring
- Configure backup/restore
- Implement authentication

### 4. Scalability
- Tăng số replicas MinIO
- Partitioning strategy
- Caching layer
- Load balancing

## 📚 Tài liệu tham khảo

- **README.md** - Tài liệu tổng quan
- **QUICKSTART.md** - Hướng dẫn nhanh
- **PROJECT_SUMMARY.md** - Tổng kết dự án
- **k8s/README.md** - Kubernetes deployment
- MinIO Docs: https://min.io/docs/
- DuckDB Docs: https://duckdb.org/docs/
- Kubernetes Docs: https://kubernetes.io/docs/

## ✅ Checklist hoàn thành

### Development Setup
- [ ] Java 21 installed
- [ ] Maven installed
- [ ] Docker installed
- [ ] Project downloaded
- [ ] Dependencies downloaded (`mvn clean install`)
- [ ] MinIO started
- [ ] Application runs successfully

### Kubernetes Setup
- [ ] Kubernetes cluster running
- [ ] kubectl configured
- [ ] MinIO deployed to K8s
- [ ] Pods are running
- [ ] Port forwarding working
- [ ] Application connects to K8s MinIO

### Verification
- [ ] Can access MinIO Console (http://localhost:9001)
- [ ] Can upload files to MinIO
- [ ] Can query data with DuckDB
- [ ] Can see results in console
- [ ] Can view files in MinIO Console

## 💡 Tips

1. **Development workflow:**
   - Dùng Docker Compose cho local dev
   - Test code locally trước khi deploy K8s
   - Use IDE debugger

2. **Debugging:**
   - Check logs trong console output
   - View MinIO logs: `docker-compose logs -f`
   - Use MinIO Console để verify uploads

3. **Performance:**
   - Increase Java heap size nếu xử lý file lớn
   - Use DuckDB's parallel processing
   - Consider partitioning data

4. **Data organization:**
   - Organize files theo date/domain
   - Use consistent naming convention
   - Document data schema

## 🆘 Support

Nếu gặp vấn đề:
1. Check troubleshooting section
2. Review logs
3. Verify all services running
4. Check port availability
5. Consult documentation

---

**Good luck with your Data Lake project! 🚀**

