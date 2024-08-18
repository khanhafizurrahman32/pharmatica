package org.example.pharmaticb.service.auth;

import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;

public interface JwtTokenService {
    String generateAccessToken(User user, LoginRequest request);

    String generateRefreshToken(User user, LoginRequest request);

    int getAccessExpiredTime();

    int getRefreshExpiredTime();
}
