# 📚 Data Lake Documentation Index

## Chào mừng đến với Data Lake Project!

Dự án này cài đặt một hệ thống Data Lake hoàn chỉnh sử dụng:
- **MinIO** - Object Storage (S3-compatible)
- **DuckDB** - Analytics Query Engine
- **Kubernetes** - Container Orchestration (3 replicas)
- **Java 21** - Application Development

---

## 📖 Tài liệu theo thứ tự đọc

### 1️⃣ Bắt đầu nhanh
- **[QUICKSTART.md](QUICKSTART.md)** ⚡
  - Hướng dẫn chạy nhanh nhất (5 phút)
  - Docker Compose setup
  - Basic commands

### 2️⃣ Hướng dẫn cài đặt chi tiết
- **[SETUP_GUIDE.md](SETUP_GUIDE.md)** 🔧
  - 3 phương án setup (Docker/K8s/Binary)
  - Troubleshooting guide
  - Commands reference
  - Checklist hoàn thành

### 3️⃣ Tài liệu tổng quan
- **[README.md](README.md)** 📘
  - Kiến trúc hệ thống
  - Tính năng đầy đủ
  - Best practices
  - API documentation

### 4️⃣ Tổng kết dự án
- **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** 📊
  - Các thành phần đã hoàn thành
  - Kiến trúc chi tiết
  - Use cases
  - Future enhancements

### 5️⃣ Kubernetes Deployment
- **[k8s/README.md](k8s/README.md)** ☸️
  - Kubernetes deployment guide
  - 3 replicas setup
  - Access instructions

---

## 🗂️ Cấu trúc Files

### 📁 Kubernetes
```
k8s/
├── minio-deployment.yaml    # MinIO StatefulSet (3 replicas)
└── README.md                 # K8s deployment guide
```

### 📁 Source Code
```
src/main/java/com/example/
├── Main.java                     # Demo application
├── config/
│   └── MinioConfig.java         # Configuration
├── service/
│   ├── MinioService.java        # MinIO operations
│   └── DuckDBService.java       # DuckDB queries
└── util/
    └── CsvDataGenerator.java    # Sample data generator
```

### 📁 Scripts
```
├── run.bat              # Run application
├── start-minio.bat      # Start MinIO locally
├── deploy-k8s.bat       # Deploy to Kubernetes
├── docker-helper.bat    # Docker management
└── docker-compose.yml   # Docker Compose config
```

### 📁 Configuration
```
├── pom.xml                           # Maven dependencies
└── src/main/resources/
    ├── logback.xml                  # Logging config
    └── application.properties       # App config
```

---

## 🎯 Quick Navigation

### Tôi muốn...

#### 🚀 Chạy ngay trong 5 phút
→ Đọc [QUICKSTART.md](QUICKSTART.md)

#### 🔧 Cài đặt đầy đủ và hiểu rõ
→ Đọc [SETUP_GUIDE.md](SETUP_GUIDE.md)

#### 📖 Hiểu kiến trúc và tính năng
→ Đọc [README.md](README.md)

#### ☸️ Deploy lên Kubernetes
→ Đọc [k8s/README.md](k8s/README.md)

#### 📊 Xem tổng quan dự án
→ Đọc [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)

#### 🐛 Gặp lỗi
→ Phần Troubleshooting trong [SETUP_GUIDE.md](SETUP_GUIDE.md)

#### 💻 Xem code
→ Mở `src/main/java/com/example/Main.java`

#### 🧪 Test thử
→ Run: `mvn exec:java -Dexec.mainClass="com.example.Main"`

---

## 🎓 Learning Path

### Beginner
1. Đọc QUICKSTART.md
2. Chạy Docker Compose
3. Run demo application
4. Explore MinIO Console

### Intermediate
1. Đọc README.md
2. Hiểu kiến trúc hệ thống
3. Modify Main.java
4. Create custom queries
5. Generate custom CSV data

### Advanced
1. Đọc PROJECT_SUMMARY.md
2. Deploy to Kubernetes
3. Scale replicas
4. Implement monitoring
5. Production hardening

---

## 📋 Feature Checklist

### MinIO Features
- ✅ Upload CSV files
- ✅ Download CSV files
- ✅ List files in bucket
- ✅ Delete files
- ✅ Stream processing
- ✅ Presigned URLs
- ✅ Auto bucket creation
- ✅ 3 replicas (K8s)
- ✅ Web console
- ✅ S3-compatible API

### DuckDB Features
- ✅ Load CSV from file
- ✅ Load CSV from stream
- ✅ SQL SELECT queries
- ✅ Aggregate functions
- ✅ JOIN operations
- ✅ Window functions
- ✅ Export to CSV
- ✅ In-memory processing
- ✅ Table management
- ✅ Schema inspection

### Application Features
- ✅ Sample data generation
- ✅ Demo scenarios
- ✅ Error handling
- ✅ Logging (SLF4J/Logback)
- ✅ Configuration management
- ✅ Multiple environments (Local/K8s)

---

## 🛠️ Quick Commands

### Start Everything
```bash
# Docker Compose
docker-compose up -d

# Build & Run
mvn clean install
mvn exec:java -Dexec.mainClass="com.example.Main"
```

### Kubernetes
```bash
# Deploy
kubectl apply -f k8s\minio-deployment.yaml

# Port Forward
kubectl port-forward -n data-lake svc/minio-service 9000:9000 9001:9001
```

### Check Status
```bash
# Docker
docker ps

# Kubernetes
kubectl get pods -n data-lake

# MinIO Console
http://localhost:9001
```

---

## 📊 Project Stats

- **Language:** Java 21
- **Build Tool:** Maven
- **Dependencies:** 5 (MinIO, DuckDB, Commons CSV, SLF4J, Logback)
- **Java Classes:** 6
- **K8s Resources:** 5 (Namespace, Secret, 2 Services, StatefulSet)
- **Documentation Files:** 5 (README, QUICKSTART, SETUP_GUIDE, PROJECT_SUMMARY, INDEX)
- **Scripts:** 4 (run, start-minio, deploy-k8s, docker-helper)
- **MinIO Replicas:** 3
- **Storage per Replica:** 10Gi

---

## 🔗 Important Links

### Access Points
- **MinIO Console:** http://localhost:9001
- **MinIO API:** http://localhost:9000
- **Credentials:** minioadmin / minioadmin123

### External Documentation
- [MinIO Docs](https://min.io/docs/)
- [DuckDB Docs](https://duckdb.org/docs/)
- [Kubernetes Docs](https://kubernetes.io/docs/)
- [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)

---

## ✅ Success Criteria

Bạn đã setup thành công khi:
- ✅ MinIO running (Docker/K8s)
- ✅ Application runs without errors
- ✅ Can upload CSV to MinIO
- ✅ Can query CSV with DuckDB
- ✅ Can see files in MinIO Console
- ✅ Can see query results in terminal

---

## 📞 Support & Troubleshooting

### Common Issues
1. **Connection refused** → Check MinIO is running
2. **Cannot resolve symbol** → Run `mvn clean install -U`
3. **Out of memory** → Increase Java heap: `-Xmx2g`
4. **Port already in use** → Stop conflicting service
5. **K8s pods not starting** → Check resources & image pull

### Detailed Help
→ See [SETUP_GUIDE.md - Troubleshooting](SETUP_GUIDE.md#troubleshooting)

---

## 🎯 Next Steps

### After successful setup:
1. ✅ Explore MinIO Console
2. ✅ Run sample queries
3. ✅ Modify Main.java
4. ✅ Create your own CSV data
5. ✅ Deploy to Kubernetes
6. ✅ Implement custom use cases

### Advanced:
1. Add REST API
2. Implement scheduling
3. Add data validation
4. Setup monitoring
5. Production deployment
6. CI/CD pipeline

---

## 📝 Documentation Files Summary

| File | Purpose | Audience |
|------|---------|----------|
| [QUICKSTART.md](QUICKSTART.md) | Fast setup (5 min) | Everyone |
| [SETUP_GUIDE.md](SETUP_GUIDE.md) | Detailed setup | Developers |
| [README.md](README.md) | Complete documentation | All users |
| [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) | Project overview | Managers/Devs |
| [k8s/README.md](k8s/README.md) | K8s deployment | DevOps |
| **INDEX.md** (this file) | Navigation guide | Everyone |

---

## 🚀 Ready to Start?

**Choose your path:**

### Path 1: Quickest Start (Docker)
```bash
1. docker-compose up -d
2. mvn clean install
3. mvn exec:java -Dexec.mainClass="com.example.Main"
```

### Path 2: Production Setup (Kubernetes)
```bash
1. kubectl apply -f k8s\minio-deployment.yaml
2. kubectl port-forward -n data-lake svc/minio-service 9000:9000
3. mvn exec:java -Dexec.mainClass="com.example.Main"
```

### Path 3: Learning Mode
```bash
1. Read SETUP_GUIDE.md
2. Follow step by step
3. Understand each component
```

---

**Happy Data Lake Building! 🎉**

Last Updated: 2024
Version: 1.0.0
Status: ✅ Production Ready

