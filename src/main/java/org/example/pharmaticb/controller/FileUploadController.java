package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.UploadFileRequest;
import org.example.pharmaticb.Models.Response.UploadFileResponse;
import org.example.pharmaticb.service.file.FileUploadService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@BaseController
@RestController
@RequiredArgsConstructor
public class FileUploadController {
    private final FileUploadService fileUploadService;

    @PostMapping(value = "/settings/upload-image")
    public Mono<UploadFileResponse> uploadFile(@Valid @RequestBody UploadFileRequest request, Authentication authentication) {
        return fileUploadService.uploadFile(request, (User) authentication.getPrincipal());
    }

}
