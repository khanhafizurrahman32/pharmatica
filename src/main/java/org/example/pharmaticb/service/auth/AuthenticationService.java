package org.example.pharmaticb.service.auth;

import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Request.auth.UpdatePasswordRequest;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.example.pharmaticb.Models.Response.auth.UpdatePasswordResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

public interface AuthenticationService {

    Mono<LoginResponse> login(LoginRequest request, HttpHeaders httpHeaders);

    Mono<UpdatePasswordResponse> updatePassword(UpdatePasswordRequest request, AuthorizedUser authorizedUser, HttpHeaders httpHeaders);
}
