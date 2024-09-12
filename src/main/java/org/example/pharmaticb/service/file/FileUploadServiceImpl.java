package org.example.pharmaticb.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.Request.UploadFileRequest;
import org.example.pharmaticb.Models.Response.FileUploadResponse;
import org.example.pharmaticb.Models.Response.UploadFileResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.service.user.UserService;
import org.example.pharmaticb.utilities.DateUtil;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {
    private final FileService fileService;
    private final UserService userService;

    @Override
    public Mono<UploadFileResponse> uploadFile(UploadFileRequest request, AuthorizedUser authorizedUser) {
        log.info("file size: {}", request.getFile().length);
        return userService.findByPhoneNumber(String.valueOf(authorizedUser.getPhoneNumber()))
                .switchIfEmpty(Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Username not found", ServiceError.USER_NOT_FOUND_ERROR)))
                .map(currentUser -> {
                    var imageUniqueId = "" + DateUtil.currentTimeInSecond();
                    currentUser.setImageUniqueId(imageUniqueId);
                    fileService.uploadFile(currentUser.getPhoneNumber(), request.getFile(), request.getContentType());
                    return UploadFileResponse.builder()
                            .fileUrl(fileService.getProfileImageUrl(currentUser.getPhoneNumber()))
                            .build();
                });
    }

    @Override
    public Mono<FileUploadResponse> uploadFile(FilePart filePart) {
        String key = generateUniqueKey(filePart.filename());
        return fileService.uploadFile(filePart, key).map(url -> FileUploadResponse.builder().key(key).url(url).build());
    }

    @Override
    public Mono<String> uploadFile(String key, byte[] bytes, String contentType) {
        return fileService.uploadReceiptFile(key, bytes, contentType);
    }

    @Override
    public Mono<byte[]> downloadFile(String key) {
        return fileService.downloadFile(key);
    }

    @Override
    public Mono<Void> deleteFile(String key) {
        return fileService.deleteFile(key);
    }

    private String generateUniqueKey(String filename) {
        return System.currentTimeMillis() + "_" + filename;
    }
}
