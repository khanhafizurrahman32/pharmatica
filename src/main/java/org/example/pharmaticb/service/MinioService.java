package org.example.pharmaticb.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {
    private final MinioClient minioClient;

    public void uploadFile(String minioBucket, String key, byte[] fileBuffer, String contentType) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucket).build());
            }

            InputStream inputStream = new ByteArrayInputStream(fileBuffer);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioBucket)
                            .object(key)
                            .stream(inputStream, fileBuffer.length, -1)
                            .contentType(contentType)
                            .build()
            );
            inputStream.close();
        } catch (Exception ex) {
            log.error("File upload error" + ex.getMessage());
        }
    }

    public String getUrl(String minioBucket, String key) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioBucket)
                            .object(key)
                            .expiry(60*60*24)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
