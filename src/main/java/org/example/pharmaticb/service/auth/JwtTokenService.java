package org.example.pharmaticb.service.auth;

import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.auth.RegistrationRequest;

public interface JwtTokenService {
    String generateAccessToken(User user, String role);

    String generateRefreshToken(User user, String role);

    int getAccessExpiredTime();

    int getRefreshExpiredTime();
}
