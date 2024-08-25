package org.example.pharmaticb.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Request.auth.UpdatePasswordRequest;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.example.pharmaticb.Models.Response.auth.UpdatePasswordResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.repositories.UserRepository;
import org.example.pharmaticb.service.user.UserService;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor

public class AuthenticationServiceImpl implements AuthenticationService{
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    @Override
    public Mono<LoginResponse> login(LoginRequest request, HttpHeaders httpHeaders) {
        return userService.findByPhoneNumber(request.getPhoneNumber())
                .filter(userDetails -> passwordEncoder.matches(request.getPassword(), userDetails.getPassword()))
                .switchIfEmpty(Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Username not found", ServiceError.INVALID_REQUEST)))
                .map(userDetails -> LoginResponse.builder()
                        .accessToken(jwtTokenService.generateAccessToken(userDetails, request))
                        .refreshToken(jwtTokenService.generateRefreshToken(userDetails, request))
                        .accessExpiredIn(jwtTokenService.getAccessExpiredTime())
                        .refreshExpiredIn(jwtTokenService.getRefreshExpiredTime())
                        .build());
    }

    @Override
    public Mono<UpdatePasswordResponse> updatePassword(UpdatePasswordRequest request, AuthorizedUser authorizedUser, HttpHeaders httpHeaders) {
        return userRepository.findById(authorizedUser.getId())
                .flatMap(user -> {
                    if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                        return userRepository.save(user)
                                .thenReturn(UpdatePasswordResponse.builder().success(true).build());
                    }
                    return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Password does not match", ServiceError.INVALID_REQUEST));
                })
                .switchIfEmpty(Mono.defer(() ->Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "User does not exist", ServiceError.INVALID_REQUEST))));
    }
}
