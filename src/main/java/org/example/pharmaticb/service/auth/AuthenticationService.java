package org.example.pharmaticb.service.auth;

import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

public interface AuthenticationService {

    Mono<LoginResponse> login(LoginRequest request, HttpHeaders httpHeaders);
}
