package org.example.pharmaticb.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.Request.UploadFileRequest;
import org.example.pharmaticb.Models.Response.UploadFileResponse;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService{
    private final FileService fileService;
    @Override
    public UploadFileResponse uploadFile(UploadFileRequest request) {
        return null;
    }
}
