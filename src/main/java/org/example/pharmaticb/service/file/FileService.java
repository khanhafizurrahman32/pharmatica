package org.example.pharmaticb.service.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void uploadFile(byte[] buffer, String contentType);
}
