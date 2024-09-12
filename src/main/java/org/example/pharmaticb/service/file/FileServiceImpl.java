package org.example.pharmaticb.service.file;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.service.DigitalOceanStorageService;
import org.example.pharmaticb.service.MinioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${minio.profile.image.path}")
    private String minioProfileImagePath;
    @Value("${minio.image.bucket}")
    private String minioImageBucket;
    @Value("${minio.endpoint}")
    private String minioEndpoint;
    @Value("${do.spaces.cdn.endpoint}")
    private String doSpacesCDNEndpoint;
    @Value("${do.spaces.bucket}")
    private String doSpacesBucket;

    private final MinioService minioService;
    private final DigitalOceanStorageService digitalOceanStorageService;

    @Override
    public void uploadFile(String phoneNumber, byte[] fileBuffer, String contentType) {
        var key = getKey(phoneNumber);
        minioService.uploadFile(minioImageBucket, key, fileBuffer, contentType);
    }

    @Override
    public Mono<String> uploadFile(FilePart filePart, String key) {
        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    long contentLength = dataBuffer.readableByteCount();
                    Flux<DataBuffer> content = Flux.just(dataBuffer);
                    return digitalOceanStorageService.uploadFile(
                            key,
                            content,
                            contentLength,
                            Objects.requireNonNull(filePart.headers().getContentType()).toString(),
                            doSpacesBucket,
                            doSpacesCDNEndpoint
                    );
                });
    }

    @Override
    public Mono<String> uploadReceiptFile(String key, byte[] bytes, String contentType) {
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);
        long contentLength = dataBuffer.readableByteCount();
        Flux<DataBuffer> content = Flux.just(dataBuffer);
        return digitalOceanStorageService.uploadFile(
                key,
                content,
                contentLength,
                contentType,
                doSpacesBucket,
                doSpacesCDNEndpoint
        );
        
    }

    private String getKey(String phoneNumber) {
        return minioProfileImagePath + phoneNumber;
    }

    @Override
    public String getProfileImageUrl(String profileImageName) {
        return minioService.getUrl(minioImageBucket, getKey(profileImageName));
    }

    @Override
    public Mono<byte[]> downloadFile(String key) {
        return digitalOceanStorageService.downloadFile(key, doSpacesBucket);
    }

    @Override
    public Mono<Void> deleteFile(String key) {
        return digitalOceanStorageService.deleteFile(key, doSpacesBucket);
    }
}
