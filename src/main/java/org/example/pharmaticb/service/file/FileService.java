package org.example.pharmaticb.service.file;

public interface FileService {

    void uploadFile(String phoneNumber, byte[] fileBuffer, String contentType);

    String getProfileImageUrl(String profileImageName);
}
