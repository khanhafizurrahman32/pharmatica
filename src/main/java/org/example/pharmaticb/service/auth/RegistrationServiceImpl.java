package org.example.pharmaticb.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.example.pharmaticb.service.user.UserService;
import org.example.pharmaticb.utilities.Exception.RegistrationException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final UserService userService;
    private final JwtTokenService jwtTokenService;


    @Override
    public Mono<LoginResponse> registrationLogin(LoginRequest loginRequest, HttpHeaders httpHeaders) {
        return findByCustomerName(loginRequest.getUserName())
                .flatMap(existingUser -> Mono.error(new RegistrationException("User already exists")))
                .switchIfEmpty(Mono.defer(() -> getLoginResponseMono(loginRequest)))
                .cast(LoginResponse.class);
    }

    private Mono<LoginResponse> getLoginResponseMono(LoginRequest request) {
        return userService.save(request)
                .thenReturn(LoginResponse.builder()
                        .accessToken(jwtTokenService.generateAccessToken(request.getUserName()))
                        .expiresIn(jwtTokenService.getExpiredTime())
                        .build());
    }

    private Mono<User> findByCustomerName(String customerName) {
        return userService.findByCustomerName(customerName);
    }
}
