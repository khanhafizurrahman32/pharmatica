package org.example.pharmaticb.service.file;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.service.minio.MinioService;
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
    public void uploadFile(String customerName, byte[] fileBuffer, String contentType) {
        var key = minioProfileImagePath + customerName;
        minioService.uploadFile(minioImageBucket, key, fileBuffer, contentType);
    }

    @Override
    public String getProfileImageUrl(String profileImageName) {
        return minioEndpoint + "/" + minioProfileImagePath + profileImageName;
    }
}
