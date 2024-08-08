package org.example.pharmaticb.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    @Value("${jwt.access-token-expiration}")
    private int accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private int refreshTokenExpiration;

    public static final String TOKEN_PROVIDER = "https://api.phramatica.com";
    private final Algorithm algorithm;

    public JwtTokenServiceImpl(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, accessTokenExpiration);
    }

    @Override
    public int getExpiredTime() {
        return this.accessTokenExpiration;
    }


    private String createToken(Map<String, Object> claims, String username, long expiration) {
        return JWT.create()
                .withIssuer(TOKEN_PROVIDER)
                .withAudience(username)
                .sign(algorithm);
    }
}
