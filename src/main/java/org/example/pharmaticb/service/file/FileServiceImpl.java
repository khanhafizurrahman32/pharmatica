package org.example.pharmaticb.service.file;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.service.MinioService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${minio.profile.image.path}")
    private String minioProfileImagePath;
    @Value("${minio.image.bucket}")
    private String minioImageBucket;
    @Value("${minio.endpoint}")
    private String minioEndpoint;


    private final MinioService minioService;

    @Override
    public void uploadFile(String phoneNumber, byte[] fileBuffer, String contentType) {
        var key = getKey(phoneNumber);
        minioService.uploadFile(minioImageBucket, key, fileBuffer, contentType);
    }

    private String getKey(String phoneNumber) {
        return minioProfileImagePath + phoneNumber;
    }

    @Override
    public String getProfileImageUrl(String profileImageName) {
        return minioService.getUrl(minioImageBucket, getKey(profileImageName));
    }
}
