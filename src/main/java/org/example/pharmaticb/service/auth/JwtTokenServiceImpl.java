package org.example.pharmaticb.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.auth.RegistrationRequest;
import org.example.pharmaticb.utilities.SecurityUtil;
import org.example.pharmaticb.utilities.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
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

    private String [] getRolesArray(String role) {
        return new String[] {role};
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
