package org.example.pharmaticb.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.UploadFileRequest;
import org.example.pharmaticb.Models.Response.UploadFileResponse;
import org.example.pharmaticb.service.user.UserService;
import org.example.pharmaticb.utilities.DateUtil;
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
    public Mono<UploadFileResponse> uploadFile(UploadFileRequest request, String customerName) {
        return userService.findByCustomerName(customerName)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Username not found")))
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
