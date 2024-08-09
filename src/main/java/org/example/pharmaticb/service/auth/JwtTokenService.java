package org.example.pharmaticb.service.auth;

import org.example.pharmaticb.Models.DB.User;

public interface JwtTokenService {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    int getAccessExpiredTime();

    int getRefreshExpiredTime();
}
