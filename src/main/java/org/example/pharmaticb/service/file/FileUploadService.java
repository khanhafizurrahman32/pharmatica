package org.example.pharmaticb.service.file;

import org.example.pharmaticb.Models.Request.UploadFileRequest;
import org.example.pharmaticb.Models.Response.UploadFileResponse;
import reactor.core.publisher.Mono;

public interface FileUploadService {
    Mono<UploadFileResponse> uploadFile(UploadFileRequest request, String customerName);
}
