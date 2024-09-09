package org.example.pharmaticb.service.auth;

import org.example.pharmaticb.Models.Request.RefreshTokenRequest;
import org.example.pharmaticb.Models.Request.auth.ForgetPasswordRequest;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Request.auth.UpdatePasswordRequest;
import org.example.pharmaticb.Models.Response.auth.ForgetPasswordResponse;
import org.example.pharmaticb.Models.Response.auth.RefreshTokenResponse;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.example.pharmaticb.Models.Response.auth.UpdatePasswordResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

public interface AuthenticationService {

    Mono<LoginResponse> login(LoginRequest request, HttpHeaders httpHeaders);

    Mono<UpdatePasswordResponse> updatePassword(UpdatePasswordRequest request, AuthorizedUser authorizedUser, HttpHeaders httpHeaders);

    Mono<ForgetPasswordResponse> forgetPassword(@Valid ForgetPasswordRequest request, HttpHeaders httpHeaders);

    Mono<RefreshTokenResponse> refreshToken(RefreshTokenRequest request);
}
