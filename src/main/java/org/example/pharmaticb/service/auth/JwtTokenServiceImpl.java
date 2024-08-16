package org.example.pharmaticb.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.utilities.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Set;

import static org.example.pharmaticb.utilities.Role.USER;

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
    public String generateAccessToken(User user) {
        return  createToken(user, accessTokenExpiration, getRolesArray(user));
    }

    @Override
    public String generateRefreshToken(User user) {
        return createToken(user, refreshTokenExpiration, getRolesArray(user));
    }

    @Override
    public int getAccessExpiredTime() {
        return this.accessTokenExpiration;
    }

    @Override
    public int getRefreshExpiredTime() {
        return this.refreshTokenExpiration;
    }

    private String createToken(User user, long expiration, String [] rolesArray) {

        try {
            return JWT.create()
                    .withIssuer(TOKEN_PROVIDER)
                    .withAudience(user.getCustomerName())
                    .withArrayClaim(SecurityUtil.TOKEN_ROLE, rolesArray)
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error creating JWT token", exception);
        }
    }

    private String [] getRolesArray(User user) {
        return Set.of(USER).stream()
                .map(Enum::name)
                .toArray(String[]::new);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
