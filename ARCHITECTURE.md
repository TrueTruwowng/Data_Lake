# Data Lake Architecture Diagrams

## 1. Overall System Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          KUBERNETES CLUSTER                              │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────┐    │
│  │               Namespace: data-lake                             │    │
│  │                                                                 │    │
│  │  ┌──────────────────────────────────────────────────────┐     │    │
│  │  │        MinIO StatefulSet (3 Replicas)                │     │    │
│  │  │                                                       │     │    │
│  │  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐│    │    │
│  │  │  │   minio-0    │  │   minio-1    │  │   minio-2    ││    │    │
│  │  │  │              │  │              │  │              ││    │    │
│  │  │  │  Container   │  │  Container   │  │  Container   ││    │    │
│  │  │  │  Port: 9000  │  │  Port: 9000  │  │  Port: 9000  ││    │    │
│  │  │  │  Port: 9001  │  │  Port: 9001  │  │  Port: 9001  ││    │    │
│  │  │  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘│    │    │
│  │  │         │                  │                  │        │    │    │
│  │  │         ▼                  ▼                  ▼        │    │    │
│  │  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐│    │    │
│  │  │  │ PVC: 10Gi    │  │ PVC: 10Gi    │  │ PVC: 10Gi    ││    │    │
│  │  │  │ /data        │  │ /data        │  │ /data        ││    │    │
│  │  │  └──────────────┘  └──────────────┘  └──────────────┘│    │    │
│  │  │         │                  │                  │        │    │    │
│  │  │         └──────────────────┴──────────────────┘        │    │    │
│  │  │                           │                            │    │    │
│  │  │              Distributed Object Storage               │    │    │
│  │  └───────────────────────────┬──────────────────────────┘     │    │
│  │                              │                                 │    │
│  │  ┌───────────────────────────▼─────────────────────────┐     │    │
│  │  │        minio-headless (ClusterIP: None)             │     │    │
│  │  │        Internal communication                        │     │    │
│  │  └───────────────────────────┬─────────────────────────┘     │    │
│  │                              │                                 │    │
│  │  ┌───────────────────────────▼─────────────────────────┐     │    │
│  │  │        minio-service (LoadBalancer)                  │     │    │
│  │  │        Port 9000 (API) / 9001 (Console)             │     │    │
│  │  └───────────────────────────┬─────────────────────────┘     │    │
│  └────────────────────────────────┼──────────────────────────────┘    │
└─────────────────────────────────────┼─────────────────────────────────┘
                                      │
                                      │ HTTP/HTTPS
                                      │
                 ┌────────────────────▼────────────────────┐
                 │      Java Application (Client)          │
                 │                                          │
                 │  ┌────────────────────────────────┐     │
                 │  │      Main.java                 │     │
                 │  │      - Demo & Orchestration    │     │
                 │  └──────────┬──────────┬──────────┘     │
                 │             │          │                 │
                 │    ┌────────▼─────┐  ┌▼──────────────┐  │
                 │    │ MinioService │  │ DuckDBService │  │
                 │    │              │  │               │  │
                 │    │ - Upload     │  │ - Load CSV    │  │
                 │    │ - Download   │  │ - Query       │  │
                 │    │ - List       │◄─┤ - Analyze     │  │
                 │    │ - Delete     │  │ - Export      │  │
                 │    └──────────────┘  └───────────────┘  │
                 └─────────────────────────────────────────┘
```

## 2. Data Flow Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                         DATA FLOW                                 │
└──────────────────────────────────────────────────────────────────┘

1. CSV DATA UPLOAD
   ┌──────────┐       ┌──────────────┐       ┌─────────────────┐
   │ CSV File │──────►│ MinioService │──────►│ MinIO Cluster   │
   │ or Data  │       │ .uploadCsv() │       │ (3 replicas)    │
   └──────────┘       └──────────────┘       └─────────────────┘
                                                      │
                                                      ▼
                                              Distributed Storage
                                              (data-lake bucket)

2. DATA QUERY & ANALYTICS
   ┌─────────────────┐       ┌──────────────┐       ┌──────────────┐
   │ MinIO Cluster   │──────►│ MinioService │──────►│ InputStream  │
   │ (CSV Storage)   │       │ .getStream() │       │              │
   └─────────────────┘       └──────────────┘       └──────┬───────┘
                                                             │
                                                             ▼
                                                    ┌──────────────┐
                                                    │ DuckDBService│
                                                    │ .loadCsv()   │
                                                    └──────┬───────┘
                                                           │
                                                           ▼
                                                    ┌──────────────┐
                                                    │ SQL Query    │
                                                    │ Processing   │
                                                    └──────┬───────┘
                                                           │
                                                           ▼
                                                    ┌──────────────┐
                                                    │   Results    │
                                                    └──────────────┘

3. ANALYTICS EXPORT
   ┌──────────────┐       ┌──────────────┐       ┌─────────────────┐
   │ Query Result │──────►│ DuckDBService│──────►│  CSV Export     │
   │              │       │ .toCSV()     │       │                 │
   └──────────────┘       └──────────────┘       └────────┬────────┘
                                                            │
                                                            ▼
                                                   ┌──────────────┐
                                                   │ MinioService │
                                                   │ .upload()    │
                                                   └──────┬───────┘
                                                          │
                                                          ▼
                                                   ┌─────────────────┐
                                                   │ MinIO Cluster   │
                                                   │ (analytics/)    │
                                                   └─────────────────┘
```

## 3. Component Interaction Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    COMPONENT INTERACTIONS                        │
└─────────────────────────────────────────────────────────────────┘

    User/Application
         │
         ▼
    ┌────────────────┐
    │   Main.java    │
    └────┬───────────┘
         │
         ├─────────────────┬──────────────────┐
         ▼                 ▼                  ▼
    ┌─────────┐      ┌──────────┐      ┌──────────┐
    │  Config │      │  MinIO   │      │  DuckDB  │
    │         │      │ Service  │      │ Service  │
    └─────────┘      └────┬─────┘      └────┬─────┘
                          │                  │
                          ▼                  │
                   ┌──────────────┐          │
                   │ MinIO Client │          │
                   │  (Library)   │          │
                   └──────┬───────┘          │
                          │                  │
                          ▼                  ▼
                   ┌──────────────┐    ┌──────────┐
                   │ MinIO Server │    │  DuckDB  │
                   │  (K8s/Docker)│    │ (In-Mem) │
                   └──────┬───────┘    └──────────┘
                          │
                          ▼
                  ┌────────────────┐
                  │ Object Storage │
                  │  (S3 Protocol) │
                  └────────────────┘
```

## 4. Deployment Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    DEPLOYMENT OPTIONS                            │
└─────────────────────────────────────────────────────────────────┘

OPTION 1: LOCAL DEVELOPMENT (Docker Compose)
┌──────────────────────────────────────────┐
│         Docker Desktop                    │
│                                          │
│  ┌────────────────────────────────┐     │
│  │   minio-datalake container     │     │
│  │   Ports: 9000, 9001            │     │
│  │   Volume: minio-data           │     │
│  └────────────────────────────────┘     │
│                 ▲                        │
│                 │                        │
│                 │ localhost:9000         │
└─────────────────┼────────────────────────┘
                  │
         ┌────────┴────────┐
         │  Java App       │
         │  (IDE/Maven)    │
         └─────────────────┘


OPTION 2: KUBERNETES PRODUCTION
┌──────────────────────────────────────────────────────────────┐
│                  Kubernetes Cluster                           │
│                                                              │
│  ┌───────────────────────────────────────────────────┐      │
│  │  Namespace: data-lake                             │      │
│  │                                                    │      │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐        │      │
│  │  │ minio-0  │  │ minio-1  │  │ minio-2  │        │      │
│  │  │ + PVC    │  │ + PVC    │  │ + PVC    │        │      │
│  │  └─────┬────┘  └─────┬────┘  └─────┬────┘        │      │
│  │        └─────────────┴─────────────┘              │      │
│  │                      │                             │      │
│  │         ┌────────────▼──────────────┐             │      │
│  │         │   LoadBalancer Service    │             │      │
│  │         └────────────┬──────────────┘             │      │
│  └──────────────────────┼───────────────────────────┘      │
└─────────────────────────┼──────────────────────────────────┘
                          │
                 ┌────────▼────────┐
                 │  External IP    │
                 │  or Port Fwd    │
                 └────────┬────────┘
                          │
                 ┌────────▼────────┐
                 │   Java App      │
                 │   (Anywhere)    │
                 └─────────────────┘
```

## 5. Data Organization Structure

```
MinIO Bucket: data-lake
│
├── sales/                      # Domain: Sales Data
│   ├── 2024/
│   │   ├── 01/
│   │   │   └── sales_data.csv
│   │   ├── 02/
│   │   └── 03/
│   └── sales_data.csv
│
├── products/                   # Domain: Product Catalog
│   └── products_data.csv
│
├── employees/                  # Domain: HR Data
│   └── employees_data.csv
│
├── analytics/                  # Domain: Analytics Results
│   ├── city_salary_summary.csv
│   └── reports/
│       ├── daily_summary.csv
│       └── monthly_summary.csv
│
├── raw/                        # Raw incoming data
│   └── uploads/
│
├── processed/                  # Processed/cleaned data
│   └── clean/
│
└── archive/                    # Historical archive
    └── 2023/
```

## 6. Query Processing Flow

```
┌─────────────────────────────────────────────────────────────┐
│              QUERY PROCESSING PIPELINE                       │
└─────────────────────────────────────────────────────────────┘

Step 1: Data Retrieval
┌──────────────┐
│ MinIO Bucket │
│ CSV Storage  │
└──────┬───────┘
       │
       ▼ MinioService.getCsvFileStream()
┌──────────────┐
│ InputStream  │
└──────┬───────┘
       │
       ▼
Step 2: Data Loading
┌──────────────────┐
│ DuckDB Memory    │
│ Table Creation   │
└──────┬───────────┘
       │
       ▼ DuckDBService.loadCsvFromStream()
┌──────────────────┐
│ In-Memory Table  │
│ (Columnar)       │
└──────┬───────────┘
       │
       ▼
Step 3: Query Execution
┌──────────────────┐
│ SQL Query        │
│ (SELECT/JOIN/    │
│  GROUP BY/etc)   │
└──────┬───────────┘
       │
       ▼ DuckDBService.executeQuery()
┌──────────────────┐
│ Query Results    │
│ (List<String[]>) │
└──────┬───────────┘
       │
       ▼
Step 4: Output
┌──────┴──────┬───────────┬──────────┐
│             │           │          │
▼             ▼           ▼          ▼
Console    Export to   Upload to   API
Output     CSV File    MinIO       Response
```

## 7. Security Architecture (Production)

```
┌─────────────────────────────────────────────────────────┐
│              SECURITY LAYERS                             │
└─────────────────────────────────────────────────────────┘

Layer 1: Network Security
┌──────────────────────────────────────┐
│  Network Policies                    │
│  - Namespace isolation               │
│  - Ingress/Egress rules             │
└──────────────────────────────────────┘

Layer 2: Authentication
┌──────────────────────────────────────┐
│  MinIO Authentication                │
│  - Access Key / Secret Key           │
│  - K8s Secrets                       │
│  - IAM Policies                      │
└──────────────────────────────────────┘

Layer 3: Authorization
┌──────────────────────────────────────┐
│  RBAC (Role-Based Access Control)    │
│  - Kubernetes RBAC                   │
│  - MinIO Bucket Policies            │
│  - Service Account Permissions       │
└──────────────────────────────────────┘

Layer 4: Encryption
┌──────────────────────────────────────┐
│  Data Encryption                     │
│  - TLS/SSL in transit                │
│  - Encryption at rest (optional)     │
│  - Certificate management            │
└──────────────────────────────────────┘

Layer 5: Monitoring & Audit
┌──────────────────────────────────────┐
│  Logging & Auditing                  │
│  - Access logs                       │
│  - Audit trails                      │
│  - Alert system                      │
└──────────────────────────────────────┘
```

## 8. Scalability Model

```
┌─────────────────────────────────────────────────────────┐
│              SCALABILITY OPTIONS                         │
└─────────────────────────────────────────────────────────┘

Horizontal Scaling (MinIO)
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│ minio-0  │  │ minio-1  │  │ minio-2  │  │ minio-3  │
└──────────┘  └──────────┘  └──────────┘  └──────────┘
     │             │             │             │
     └─────────────┴─────────────┴─────────────┘
                   │
           Distributed Storage
           (Increased capacity)

Vertical Scaling (Resources)
┌────────────────────────────────────┐
│  Pod Resources                     │
│  ┌──────────────────────────────┐  │
│  │ CPU: 2 → 4 cores             │  │
│  │ Memory: 4Gi → 8Gi            │  │
│  │ Storage: 10Gi → 50Gi         │  │
│  └──────────────────────────────┘  │
└────────────────────────────────────┘

Application Scaling
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  App Pod 1   │  │  App Pod 2   │  │  App Pod 3   │
│  (DuckDB)    │  │  (DuckDB)    │  │  (DuckDB)    │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       └──────────────────┴──────────────────┘
                          │
                   Load Balancer
                          │
                      MinIO API
```

---

**Note:** These diagrams provide a visual overview of the Data Lake architecture.
Refer to the documentation files for detailed implementation instructions.

