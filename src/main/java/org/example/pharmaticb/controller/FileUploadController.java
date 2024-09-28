package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.Request.UploadFileRequest;
import org.example.pharmaticb.Models.Response.FileUploadResponse;
import org.example.pharmaticb.Models.Response.UploadFileResponse;
import org.example.pharmaticb.service.file.FileUploadService;
import org.example.pharmaticb.utilities.Utility;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.security.Principal;

@BaseController
@RestController
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {
    private final FileUploadService fileUploadService;

    @PostMapping(value = "/settings/file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<FileUploadResponse> uploadFile(@RequestPart("file") FilePart filePart) {
        return fileUploadService.uploadFile(filePart);
    }

    @GetMapping("/key")
    public Mono<byte[]> downloadFile(@PathVariable String key) {
        return fileUploadService.downloadFile(key);
    }

    @DeleteMapping("/key")
    public Mono<Void> deleteFile(@PathVariable String key) {
        return fileUploadService.deleteFile(key);
    }
}
