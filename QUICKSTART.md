# Quick Start Guide - Data Lake với MinIO và DuckDB

## 🚀 Bắt đầu nhanh

### Bước 1: Cài đặt dependencies

```bash
cd E:\StudyDoc\NAM3\PTUDDN\Data_Lake
mvn clean install
```

### Bước 2: Khởi động MinIO

#### Option A: Sử dụng Docker (Khuyến nghị)
```bash
docker run -d -p 9000:9000 -p 9001:9001 ^
  --name minio-datalake ^
  -e "MINIO_ROOT_USER=minioadmin" ^
  -e "MINIO_ROOT_PASSWORD=minioadmin123" ^
  quay.io/minio/minio server /data --console-address ":9001"
```

#### Option B: Sử dụng script
```bash
start-minio.bat
```

#### Option C: Download MinIO binary
1. Tải về: https://dl.min.io/server/minio/release/windows-amd64/minio.exe
2. Chạy: `minio.exe server E:\minio-data --console-address ":9001"`

### Bước 3: Kiểm tra MinIO

Mở browser và truy cập:
- MinIO Console: http://localhost:9001
- Username: `minioadmin`
- Password: `minioadmin123`

### Bước 4: Chạy ứng dụng

```bash
# Option 1: Sử dụng script
run.bat

# Option 2: Chạy trực tiếp với Maven
mvn exec:java -Dexec.mainClass="com.example.Main"

# Option 3: Sử dụng IDE (IntelliJ/Eclipse)
# Mở Main.java và nhấn Run
```

## 📊 Kết quả mong đợi

Sau khi chạy thành công, bạn sẽ thấy:

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

Query 1: All sales records
=== Query Results ===
order_id | customer_name | product | quantity | price | order_date
------------------------------------------------------------------------
1 | Nguyen Van A | Laptop | 2 | 15000000 | 2024-01-15
...

Query 2: Total sales by customer
customer_name | total_orders | total_amount
...
```

## ☸️ Triển khai lên Kubernetes

### Bước 1: Kiểm tra Kubernetes cluster
```bash
kubectl cluster-info
```

### Bước 2: Deploy MinIO
```bash
# Option 1: Sử dụng script
deploy-k8s.bat

# Option 2: Apply trực tiếp
kubectl apply -f k8s\minio-deployment.yaml
```

### Bước 3: Kiểm tra deployment
```bash
kubectl get pods -n data-lake
kubectl get svc -n data-lake
```

### Bước 4: Port forward để truy cập từ local
```bash
kubectl port-forward -n data-lake svc/minio-service 9000:9000 9001:9001
```

### Bước 5: Cập nhật code để sử dụng K8s endpoint

Trong file `Main.java`, thay đổi:
```java
// Từ:
MinioConfig config = MinioConfig.getDefaultConfig();

// Thành:
MinioConfig config = MinioConfig.getK8sConfig();
```

## 🔍 Các tính năng chính

### 1. Upload CSV vào MinIO
```java
MinioService minioService = new MinioService(config);
minioService.uploadCsvContent("data/mydata.csv", csvContent);
```

### 2. Query dữ liệu với DuckDB
```java
DuckDBService duckDBService = new DuckDBService();
duckDBService.loadCsvFromFile("mytable", "data.csv");
duckDBService.printQueryResults("SELECT * FROM mytable");
```

### 3. Advanced Analytics
```java
duckDBService.printQueryResults(
    "SELECT city, AVG(salary) as avg_salary " +
    "FROM employees GROUP BY city"
);
```

### 4. Export kết quả
```java
duckDBService.executeQueryToCSV(query, "output.csv");
minioService.uploadCsvFile("results/output.csv", file);
```

## 🛠️ Troubleshooting

### Lỗi: Connection refused (MinIO)
**Giải pháp:**
- Kiểm tra MinIO đang chạy: `docker ps` hoặc Task Manager
- Kiểm tra port 9000 không bị chiếm dụng
- Restart MinIO service

### Lỗi: Cannot resolve symbol
**Giải pháp:**
```bash
mvn clean install -U
# Hoặc trong IntelliJ: File -> Invalidate Caches -> Invalidate and Restart
```

### Lỗi: Out of memory
**Giải pháp:**
```bash
# Tăng Java heap size
set MAVEN_OPTS=-Xmx2g
mvn exec:java -Dexec.mainClass="com.example.Main"
```

### Lỗi Kubernetes: ImagePullBackOff
**Giải pháp:**
```bash
# Kiểm tra internet connection
# Hoặc pull image trước:
docker pull quay.io/minio/minio:latest
```

## 📁 Cấu trúc dữ liệu trong MinIO

Sau khi chạy demo, cấu trúc bucket sẽ như sau:

```
data-lake/
├── sales/
│   └── sales_data.csv
├── products/
│   └── products_data.csv
├── employees/
│   └── employees_data.csv
└── analytics/
    └── city_salary_summary.csv
```

## 🔐 Bảo mật (Production)

1. **Thay đổi credentials:**
   - Edit `k8s/minio-deployment.yaml`
   - Thay đổi `rootUser` và `rootPassword`

2. **Enable TLS:**
   - Cấu hình certificates cho MinIO
   - Update endpoint thành `https://`

3. **Network policies:**
   - Restrict access to MinIO service
   - Chỉ cho phép pods trong namespace truy cập

## 📚 Tài liệu API

### MinioService Methods
- `uploadCsvFile(String objectName, File file)` - Upload file
- `uploadCsvContent(String objectName, String content)` - Upload string content
- `downloadCsvFile(String objectName, String path)` - Download file
- `getCsvFileStream(String objectName)` - Get InputStream
- `listCsvFiles()` - List all CSV files
- `deleteCsvFile(String objectName)` - Delete file
- `fileExists(String objectName)` - Check existence
- `getPresignedUrl(String objectName, int seconds)` - Get temporary URL

### DuckDBService Methods
- `loadCsvFromFile(String tableName, String filePath)` - Load CSV into table
- `executeQuery(String query)` - Execute SELECT query
- `executeQueryToCSV(String query, String outputPath)` - Export results
- `printQueryResults(String query)` - Print to console
- `showTableInfo(String tableName)` - Show table schema
- `listTables()` - List all tables
- `dropTable(String tableName)` - Drop table

## 💡 Best Practices

1. **Naming Convention:**
   - Files: `{domain}_{date}_{version}.csv`
   - Tables: lowercase với underscores

2. **Data Organization:**
   - Sử dụng prefixes: `sales/2024/01/data.csv`
   - Nhóm theo domain và time period

3. **Performance:**
   - Batch upload nhiều files
   - Use prepared statements cho queries
   - Close connections properly

4. **Monitoring:**
   - Check MinIO metrics qua console
   - Monitor pod resources trong K8s
   - Set up alerts for failures

## 🎯 Next Steps

1. **Thêm tính năng:**
   - Scheduled data ingestion
   - Data validation
   - Error handling & retry logic
   - Metrics và monitoring

2. **Tối ưu:**
   - Connection pooling
   - Batch processing
   - Compression
   - Partitioning

3. **CI/CD:**
   - Automated testing
   - Docker images
   - Helm charts
   - GitOps với ArgoCD

## 📞 Support

Nếu gặp vấn đề, check:
1. README.md - Tài liệu chi tiết
2. k8s/README.md - Hướng dẫn Kubernetes
3. Logs: `kubectl logs -n data-lake minio-0`
4. MinIO console: http://localhost:9001

