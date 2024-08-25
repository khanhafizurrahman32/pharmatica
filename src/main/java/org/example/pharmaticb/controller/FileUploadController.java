package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.Request.UploadFileRequest;
import org.example.pharmaticb.Models.Response.UploadFileResponse;
import org.example.pharmaticb.service.file.FileUploadService;
import org.example.pharmaticb.utilities.Utility;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.security.Principal;

@BaseController
@RestController
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {
    private final FileUploadService fileUploadService;

    @PostMapping(value = "/settings/upload-file")
    public Mono<UploadFileResponse> uploadFile(@Valid @RequestBody UploadFileRequest request, Principal principal) {
        return fileUploadService.uploadFile(request, Utility.extractAuthorizedUserFromPrincipal(principal));
    }
}
