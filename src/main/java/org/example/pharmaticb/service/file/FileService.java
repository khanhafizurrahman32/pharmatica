package org.example.pharmaticb.service.file;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileService {

    void uploadFile(String phoneNumber, byte[] fileBuffer, String contentType);
    Mono<String> uploadFile(FilePart filePart, String key);

    String getProfileImageUrl(String profileImageName);

    Mono<byte[]> downloadFile(String key);

    Mono<Void> deleteFile(String key);
}
