package com.example.service;

import com.example.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing MinIO operations - storing and retrieving CSV files
 */
public class MinioService {
    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);
    private final MinioClient minioClient;
    private final String bucketName;

    public MinioService(MinioConfig config) {
        this.bucketName = config.getBucketName();
        this.minioClient = MinioClient.builder()
            .endpoint(config.getEndpoint())
            .credentials(config.getAccessKey(), config.getSecretKey())
            .build();

        try {
            createBucketIfNotExists();
        } catch (Exception e) {
            logger.error("Failed to initialize MinIO bucket", e);
            throw new RuntimeException("Failed to initialize MinIO", e);
        }
    }

    /**
     * Create bucket if it doesn't exist
     */
    private void createBucketIfNotExists() throws Exception {
        boolean exists = minioClient.bucketExists(
            BucketExistsArgs.builder().bucket(bucketName).build()
        );

        if (!exists) {
            minioClient.makeBucket(
                MakeBucketArgs.builder().bucket(bucketName).build()
            );
            logger.info("Created bucket: {}", bucketName);
        } else {
            logger.info("Bucket already exists: {}", bucketName);
        }
    }

    /**
     * Upload CSV file to MinIO
     */
    public void uploadCsvFile(String objectName, File file) throws Exception {
        minioClient.uploadObject(
            UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename(file.getAbsolutePath())
                .contentType("text/csv")
                .build()
        );
        logger.info("Uploaded file: {} to bucket: {}", objectName, bucketName);
    }

    /**
     * Upload CSV content from string
     */
    public void uploadCsvContent(String objectName, String csvContent) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(csvContent.getBytes());
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(bais, csvContent.getBytes().length, -1)
                .contentType("text/csv")
                .build()
        );
        logger.info("Uploaded CSV content as: {}", objectName);
    }

    /**
     * Download CSV file from MinIO
     */
    public File downloadCsvFile(String objectName, String downloadPath) throws Exception {
        minioClient.downloadObject(
            DownloadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename(downloadPath)
                .build()
        );
        logger.info("Downloaded file: {} to: {}", objectName, downloadPath);
        return new File(downloadPath);
    }

    /**
     * Get CSV file as InputStream
     */
    public InputStream getCsvFileStream(String objectName) throws Exception {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build()
        );
    }

    /**
     * List all CSV files in the bucket
     */
    public List<String> listCsvFiles() {
        List<String> files = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();
                if (objectName.endsWith(".csv")) {
                    files.add(objectName);
                }
            }
        } catch (Exception e) {
            logger.error("Error listing CSV files", e);
        }
        return files;
    }

    /**
     * Delete CSV file from MinIO
     */
    public void deleteCsvFile(String objectName) throws Exception {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build()
        );
        logger.info("Deleted file: {}", objectName);
    }

    /**
     * Check if file exists
     */
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get presigned URL for temporary access
     */
    public String getPresignedUrl(String objectName, int expirySeconds) throws Exception {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(io.minio.http.Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry(expirySeconds)
                .build()
        );
    }
}

