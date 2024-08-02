package org.example.pharmaticb.service.file;

import org.example.pharmaticb.Models.Request.UploadFileRequest;
import org.example.pharmaticb.Models.Response.UploadFileResponse;

public interface FileUploadService {
    UploadFileResponse uploadFile(UploadFileRequest request);
}
