package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.service.file.FileUploadService;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@BaseController
@RestController
@RequiredArgsConstructor
public class FileUploadController {
    private final FileUploadService fileUploadService;


}
