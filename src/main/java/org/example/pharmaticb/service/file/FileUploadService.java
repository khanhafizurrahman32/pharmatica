package org.example.pharmaticb.service.file;

import org.example.pharmaticb.Models.Request.UploadFileRequest;
import org.example.pharmaticb.Models.Response.UploadFileResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import reactor.core.publisher.Mono;

public interface FileUploadService {
    Mono<UploadFileResponse> uploadFile(UploadFileRequest request, AuthorizedUser authorizedUser);
}
