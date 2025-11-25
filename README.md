# Data Lake Project - MinIO + DuckDB

## Tổng quan

Dự án Data Lake sử dụng:
- **MinIO**: Lưu trữ object storage cho dữ liệu CSV
- **DuckDB**: Query engine để phân tích dữ liệu CSV
- **Kubernetes**: Triển khai MinIO với 3 replicas để đảm bảo high availability

## Kiến trúc

```
┌─────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              MinIO StatefulSet (3 Replicas)          │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐           │   │
│  │  │ MinIO-0  │  │ MinIO-1  │  │ MinIO-2  │           │   │
│  │  └────┬─────┘  └────┬─────┘  └────┬─────┘           │   │
│  │       │             │             │                  │   │
│  │       └─────────────┴─────────────┘                  │   │
│  │                     │                                │   │
│  │              MinIO Service                           │   │
│  └──────────────────────┬───────────────────────────────┘   │
└─────────────────────────┼───────────────────────────────────┘
                          │
              ┌───────────┴───────────┐
              │                       │
        ┌─────▼─────┐         ┌──────▼──────┐
        │   Java    │         │   DuckDB    │
        │   MinIO   │────────▶│   Query     │
        │  Service  │         │   Engine    │
        └───────────┘         └─────────────┘
              │
              ▼
        CSV Data Storage
```

## Cấu trúc thư mục

```
Data_Lake/
├── k8s/                          # Kubernetes deployment files
│   ├── minio-deployment.yaml    # MinIO StatefulSet với 3 replicas
│   └── README.md                 # Hướng dẫn triển khai K8s
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/
│       │       ├── Main.java                    # Demo application
│       │       ├── config/
│       │       │   └── MinioConfig.java         # MinIO configuration
│       │       └── service/
│       │           ├── MinioService.java        # MinIO operations
│       │           └── DuckDBService.java       # DuckDB queries
│       └── resources/
└── pom.xml                       # Maven dependencies
```

## Yêu cầu

- Java 21+
- Maven 3.6+
- MinIO server (local hoặc K8s)
- Kubernetes cluster (cho production deployment)

## Cài đặt

### 1. Build project

```bash
mvn clean install
```

### 2. Chạy MinIO local (cho development)

#### Option A: Sử dụng Docker
```bash
docker run -p 9000:9000 -p 9001:9001 \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin123" \
  quay.io/minio/minio server /data --console-address ":9001"
```

#### Option B: Tải MinIO binary
```bash
# Download MinIO
wget https://dl.min.io/server/minio/release/windows-amd64/minio.exe

# Run MinIO
minio.exe server E:\data --console-address ":9001"
```

### 3. Triển khai trên Kubernetes (Production)

Xem hướng dẫn chi tiết tại: [k8s/README.md](k8s/README.md)

```bash
kubectl apply -f k8s/minio-deployment.yaml
kubectl port-forward -n data-lake svc/minio-service 9000:9000 9001:9001
```

## Chạy ứng dụng

### Development mode (MinIO local)

```bash
mvn clean compile exec:java -Dexec.mainClass="com.example.Main"
```

### Production mode (MinIO trên K8s)

Sửa code trong Main.java:
```java
MinioConfig config = MinioConfig.getK8sConfig(); // Thay vì getDefaultConfig()
```

## Tính năng

### MinIO Service

- ✓ Upload CSV files
- ✓ Download CSV files
- ✓ List CSV files
- ✓ Delete CSV files
- ✓ Check file existence
- ✓ Generate presigned URLs
- ✓ Auto bucket creation

### DuckDB Service

- ✓ Load CSV từ file
- ✓ Load CSV từ InputStream
- ✓ Execute SELECT queries
- ✓ Export kết quả ra CSV
- ✓ Table management (create, drop, describe)
- ✓ Join multiple tables
- ✓ Aggregate queries
- ✓ Advanced analytics

## Demo scenarios

### 1. Upload CSV data vào MinIO
```java
minioService.uploadCsvContent("sales/sales_data.csv", csvContent);
```

### 2. List files trong bucket
```java
List<String> files = minioService.listCsvFiles();
```

### 3. Query data với DuckDB
```java
duckDBService.loadCsvFromFile("sales", "sales_data.csv");
duckDBService.printQueryResults("SELECT * FROM sales");
```

### 4. Advanced analytics
```java
duckDBService.printQueryResults(
    "SELECT customer, SUM(amount) as total " +
    "FROM sales GROUP BY customer ORDER BY total DESC"
);
```

### 5. Export kết quả analytics
```java
duckDBService.executeQueryToCSV(query, "output.csv");
minioService.uploadCsvFile("analytics/output.csv", file);
```

## Cấu hình

### MinIO Configuration

```java
// Local development
MinioConfig config = new MinioConfig(
    "http://localhost:9000",
    "minioadmin",
    "minioadmin123",
    "data-lake"
);

// Kubernetes
MinioConfig config = new MinioConfig(
    "http://minio-service.data-lake.svc.cluster.local:9000",
    "minioadmin",
    "minioadmin123",
    "data-lake"
);
```

## Best Practices

1. **Tổ chức dữ liệu**: Sử dụng prefix/folder structure trong MinIO
   - `sales/2024/01/data.csv`
   - `products/category_a/data.csv`
   - `analytics/reports/summary.csv`

2. **Đặt tên file**: Sử dụng naming convention rõ ràng
   - `{domain}_{date}_{version}.csv`
   - `sales_20240115_v1.csv`

3. **Backup**: MinIO với 3 replicas đảm bảo data redundancy

4. **Security**: 
   - Thay đổi default credentials
   - Sử dụng presigned URLs cho temporary access
   - Enable encryption at rest

5. **Performance**:
   - Batch upload cho nhiều files
   - Use connection pooling
   - Cache DuckDB query results

## Monitoring

### MinIO Health Check
```bash
kubectl get pods -n data-lake
kubectl logs -n data-lake minio-0
```

### MinIO Console
- URL: http://localhost:9001
- Username: minioadmin
- Password: minioadmin123

## Troubleshooting

### Connection refused
- Kiểm tra MinIO service đang chạy
- Verify port forwarding (nếu dùng K8s)
- Check firewall settings

### Out of memory
- Tăng Java heap size: `-Xmx2g`
- Limit DuckDB query result size
- Process data in batches

### CSV parsing errors
- Verify CSV format
- Check encoding (UTF-8)
- Validate delimiter và quote characters

## Tài liệu tham khảo

- [MinIO Documentation](https://min.io/docs/minio/linux/index.html)
- [DuckDB Documentation](https://duckdb.org/docs/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)

## License

MIT License

