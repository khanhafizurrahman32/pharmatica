package org.example.pharmaticb.service.file;

import org.example.pharmaticb.Models.Request.UploadFileRequest;
import org.example.pharmaticb.Models.Response.FileUploadResponse;
import org.example.pharmaticb.Models.Response.UploadFileResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileUploadService {

    Mono<FileUploadResponse> uploadFile(FilePart filePart);

    Mono<String> uploadFile(String key, byte[] bytes, String contentType);

    Mono<byte[]> downloadFile(String key);

    Mono<Void> deleteFile(String key);
}
