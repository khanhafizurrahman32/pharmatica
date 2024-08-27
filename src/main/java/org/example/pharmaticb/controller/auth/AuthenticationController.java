package org.example.pharmaticb.controller.auth;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Request.auth.UpdatePasswordRequest;
import org.example.pharmaticb.Models.Response.auth.UpdatePasswordResponse;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.example.pharmaticb.controller.BaseController;
import org.example.pharmaticb.service.auth.AuthenticationService;
import org.example.pharmaticb.utilities.Utility;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.security.Principal;

@BaseController
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/auth/login")
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest request, @RequestHeader HttpHeaders httpHeaders) {
        return authenticationService.login(request, httpHeaders);
    }

    @PostMapping("/auth/update-password")
    public Mono<UpdatePasswordResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest request,
                                                       Principal principal,
                                                       @RequestHeader HttpHeaders httpHeaders) {
        return authenticationService.updatePassword(request, Utility.extractAuthorizedUserFromPrincipal(principal), httpHeaders);
    }
}
