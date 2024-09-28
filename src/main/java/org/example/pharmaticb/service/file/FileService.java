package org.example.pharmaticb.service.file;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileService {

    Mono<String> uploadFile(FilePart filePart, String key);

    Mono<String> uploadReceiptFile(String key, byte[] bytes, String contentType);

    Mono<byte[]> downloadFile(String key);

    Mono<Void> deleteFile(String key);
}
