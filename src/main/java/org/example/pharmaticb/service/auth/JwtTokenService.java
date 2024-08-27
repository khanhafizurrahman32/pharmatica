package org.example.pharmaticb.service.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.pharmaticb.Models.DB.User;

public interface JwtTokenService {
    String generateAccessToken(User user, String role);

    String generateRefreshToken(User user, String role);

    int getAccessExpiredTime();

    int getRefreshExpiredTime();

    DecodedJWT getDecodedJwtToken(String token);
}
