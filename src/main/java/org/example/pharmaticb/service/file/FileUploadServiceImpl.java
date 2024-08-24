package org.example.pharmaticb.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.UploadFileRequest;
import org.example.pharmaticb.Models.Response.UploadFileResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.service.user.UserService;
import org.example.pharmaticb.utilities.DateUtil;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {
    private final FileService fileService;
    private final UserService userService;

    @Override
    public Mono<UploadFileResponse> uploadFile(UploadFileRequest request, AuthorizedUser authorizedUser) {
        return userService.findByCustomerName(String.valueOf(authorizedUser.getPhone()))
                .switchIfEmpty(Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Username not found", ServiceError.USER_NOT_FOUND_ERROR)))
                .map(currentUser -> {
                    var imageUniqueId = "" + DateUtil.currentTimeInSecond();
                    currentUser.setImageUniqueId(imageUniqueId);
                    fileService.uploadFile(currentUser.getCustomerName(), request.getFile(), request.getContentType());
                    return UploadFileResponse.builder()
                            .fileUrl(fileService.getProfileImageUrl(currentUser.getCustomerName()))
                            .build();
                });
    }

    private String getProfileImageName(User currentUser) {
        var key = !StringUtils.hasText(currentUser.getImageUniqueId()) ? "" : "_" + currentUser.getImageUniqueId();
        return currentUser.getCustomerName() + key;
    }
}
