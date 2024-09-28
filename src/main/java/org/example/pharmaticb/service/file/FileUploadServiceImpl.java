package org.example.pharmaticb.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.Response.FileUploadResponse;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {
    private final FileService fileService;

    @Override
    public Mono<FileUploadResponse> uploadFile(FilePart filePart) {
        String key = generateUniqueKey(filePart.filename());
        return fileService.uploadFile(filePart, key).map(url -> FileUploadResponse.builder().key(key).url(url).build());
    }

    @Override
    public Mono<String> uploadFile(String key, byte[] bytes, String contentType) {
        return fileService.uploadReceiptFile(key, bytes, contentType);
    }

    @Override
    public Mono<byte[]> downloadFile(String key) {
        return fileService.downloadFile(key);
    }

    @Override
    public Mono<Void> deleteFile(String key) {
        return fileService.deleteFile(key);
    }

    private String generateUniqueKey(String filename) {
        return System.currentTimeMillis() + "_" + filename;
    }
}
