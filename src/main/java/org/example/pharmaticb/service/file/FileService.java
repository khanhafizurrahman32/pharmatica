package org.example.pharmaticb.service.file;

public interface FileService {

    void uploadFile(String customerName, byte[] fileBuffer, String contentType);

    String getProfileImageUrl(String profileImageName);
}
