package org.example.pharmaticb.utilities.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.example.pharmaticb.service.auth.JwtTokenServiceImpl.TOKEN_PROVIDER;

@Slf4j
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private static final String DEFAULT_ROLE = "ROLE_USER";
    private final Algorithm tokenAlgorithm;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        var authToken = authentication.getCredentials().toString();
        if (verifyToken(authToken)) {
            return Mono.just(new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), null, getAuthorities()));
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> DEFAULT_ROLE);
    }

    private boolean verifyToken(String authToken) {
        JWTVerifier verifier = JWT.require(this.tokenAlgorithm).withIssuer(TOKEN_PROVIDER).build();
        DecodedJWT jwt = verifier.verify(authToken);

        List<String> audiences = jwt.getAudience();
        return !ObjectUtils.isEmpty(audiences);
    }
}
