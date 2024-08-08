package org.example.pharmaticb.service.auth;

public interface JwtTokenService {
    String generateAccessToken(String username);
    int getExpiredTime();
}
