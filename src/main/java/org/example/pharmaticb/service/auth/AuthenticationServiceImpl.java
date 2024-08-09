package org.example.pharmaticb.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.example.pharmaticb.service.user.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor

public class AuthenticationServiceImpl implements AuthenticationService{
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Override
    public Mono<LoginResponse> login(LoginRequest request, HttpHeaders httpHeaders) {
        return userService.findByCustomerName(request.getUserName())
                .filter(userDetails -> passwordEncoder.matches(request.getPassword(), userDetails.getPassword()))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Username not found")))
                .map(userDetails -> LoginResponse.builder()
                        .accessToken(jwtTokenService.generateAccessToken(userDetails))
                        .refreshToken(jwtTokenService.generateRefreshToken(userDetails))
                        .accessExpiredIn(jwtTokenService.getAccessExpiredTime())
                        .refreshExpiredIn(jwtTokenService.getRefreshExpiredTime())
                        .build());
    }
}
