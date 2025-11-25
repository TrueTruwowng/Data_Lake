# MinIO Deployment on Kubernetes

## Triển khai MinIO trên K8s với 3 replicas

### Yêu cầu
- Kubernetes cluster đang chạy
- kubectl được cấu hình đúng

### Các bước triển khai

1. **Áp dụng cấu hình MinIO:**
```bash
kubectl apply -f minio-deployment.yaml
```

2. **Kiểm tra trạng thái pods:**
```bash
kubectl get pods -n data-lake
```

3. **Kiểm tra services:**
```bash
kubectl get svc -n data-lake
```

4. **Truy cập MinIO Console:**
```bash
# Port forward để truy cập từ local machine
kubectl port-forward -n data-lake svc/minio-service 9000:9000 9001:9001
```

Sau đó truy cập:
- MinIO API: http://localhost:9000
- MinIO Console: http://localhost:9001
- Credentials: minioadmin / minioadmin123

### Cấu trúc

- **Namespace:** data-lake
- **StatefulSet:** 3 replicas của MinIO
- **Services:**
  - minio-service: LoadBalancer service cho external access
  - minio-headless: Headless service cho internal communication
- **Storage:** PersistentVolumeClaim 10Gi per replica

### Lưu ý

- Thay đổi credentials trong Secret trước khi deploy production
- Điều chỉnh storage size theo nhu cầu
- Có thể thay đổi LoadBalancer thành NodePort hoặc ClusterIP tùy môi trường

