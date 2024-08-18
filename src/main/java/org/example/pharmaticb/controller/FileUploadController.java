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
@Slf4j
public class FileUploadController {
    private final FileUploadService fileUploadService;

    @PostMapping(value = "/settings/upload-file")
    public Mono<UploadFileResponse> uploadFile(@Valid @RequestBody UploadFileRequest request, Authentication authentication) {
        log.info("String: {}", (String) authentication.getPrincipal());
        return fileUploadService.uploadFile(request, (String) authentication.getPrincipal());
    }
}
