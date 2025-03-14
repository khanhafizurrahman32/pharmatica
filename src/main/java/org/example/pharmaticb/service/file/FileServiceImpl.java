package org.example.pharmaticb.service.file;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.service.DigitalOceanStorageService;
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
    @Value("${do.spaces.cdn.endpoint}")
    private String doSpacesCDNEndpoint;
    @Value("${do.spaces.bucket}")
    private String doSpacesBucket;


    private final DigitalOceanStorageService digitalOceanStorageService;

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

    @Override
    public Mono<byte[]> downloadFile(String key) {
        return digitalOceanStorageService.downloadFile(key, doSpacesBucket);
    }

    @Override
    public Mono<Void> deleteFile(String key) {
        return digitalOceanStorageService.deleteFile(key, doSpacesBucket);
    }
}
