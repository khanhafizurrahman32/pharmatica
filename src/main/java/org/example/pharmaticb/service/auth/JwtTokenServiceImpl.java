package org.example.pharmaticb.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.utilities.SecurityUtil;
import org.example.pharmaticb.utilities.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    @Value("${jwt.secret-key}")
    private String secretKey;

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
    public String generateAccessToken(User user, String role) {
        return createToken(user, accessTokenExpiration, role);
    }

    @Override
    public String generateRefreshToken(User user, String role) {
        return createToken(user, refreshTokenExpiration, role);
    }

    @Override
    public int getAccessExpiredTime() {
        return this.accessTokenExpiration;
    }

    @Override
    public int getRefreshExpiredTime() {
        return this.refreshTokenExpiration;
    }

    private String createToken(User user, long expiration, String role) {

        try {
            return JWT.create()
                    .withIssuer(TOKEN_PROVIDER)
                    .withAudience(user.getPhoneNumber())
                    .withClaim(Utility.USER_ID, user.getId())
                    .withClaim(SecurityUtil.TOKEN_ROLE, role)
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error creating JWT token", exception);
        }
    }

    @Override
    public DecodedJWT getDecodedJwtToken(String token) {
        JWTVerifier verifier = JWT.require(this.algorithm).withIssuer(TOKEN_PROVIDER).build();
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new InternalException(HttpStatus.UNAUTHORIZED, "Code mismatch", "Code error");
        }
    }
}
