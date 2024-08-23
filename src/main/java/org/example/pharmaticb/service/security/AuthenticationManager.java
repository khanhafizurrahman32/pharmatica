package org.example.pharmaticb.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.exception.InternalException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.pharmaticb.service.auth.JwtTokenServiceImpl.TOKEN_PROVIDER;
import static org.example.pharmaticb.utilities.SecurityUtil.TOKEN_CUSTOMER_NAME;
import static org.example.pharmaticb.utilities.SecurityUtil.TOKEN_ROLE;

@Slf4j
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final Algorithm tokenAlgorithm;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("authenticate");
        var authToken = authentication.getCredentials().toString();
        DecodedJWT jwt = getDecodedJwtToken(authToken);
        String customerName = jwt.getAudience().get(0);
        if (StringUtils.hasText(customerName)) {
            return Mono.just(new UsernamePasswordAuthenticationToken(customerName, null, getAuthorities(jwt.getClaim(TOKEN_ROLE).asList(String.class))));
        }
        return Mono.just(authentication);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private DecodedJWT getDecodedJwtToken(String authToken) {
        JWTVerifier verifier = JWT.require(this.tokenAlgorithm).withIssuer(TOKEN_PROVIDER).build();
        try {
            return verifier.verify(authToken);
        } catch (JWTVerificationException ex) {
            log.error("Jwt verifier token {}", ex.toString());
            throw  new InternalException(HttpStatus.UNAUTHORIZED, "Code mismatch", "Code error");
        }
    }
}
