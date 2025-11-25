package com.example.config;

/**
 * Configuration class for MinIO connection settings
 */
public class MinioConfig {
    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String bucketName;

    public MinioConfig(String endpoint, String accessKey, String secretKey, String bucketName) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;
    }

    // Default configuration for local development
    public static MinioConfig getDefaultConfig() {
        return new MinioConfig(
            "http://localhost:9000",
            "minioadmin",
            "minioadmin123",
            "data-lake"
        );
    }

    // Configuration for Kubernetes deployment
    public static MinioConfig getK8sConfig() {
        return new MinioConfig(
            "http://minio-service.data-lake.svc.cluster.local:9000",
            "minioadmin",
            "minioadmin123",
            "data-lake"
        );
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }
}

