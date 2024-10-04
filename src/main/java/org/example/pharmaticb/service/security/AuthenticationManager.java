package org.example.pharmaticb.service.security;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.example.pharmaticb.service.auth.JwtTokenService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.example.pharmaticb.utilities.SecurityUtil.TOKEN_ROLE;
import static org.example.pharmaticb.utilities.Utility.ROLE_PREFIX;
import static org.example.pharmaticb.utilities.Utility.USER_ID;

@Slf4j
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final Algorithm tokenAlgorithm;
    private final JwtTokenService jwtTokenService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        var authToken = authentication.getCredentials().toString();
        DecodedJWT jwt = jwtTokenService.getDecodedJwtToken(authToken);
        String phoneNumber = jwt.getAudience().get(0);
        Long userId = jwt.getClaim(USER_ID).asLong();
        String role = jwt.getClaim(TOKEN_ROLE).asString();
        AuthorizedUser authorizedUser = new AuthorizedUser(userId, phoneNumber, ROLE_PREFIX + role);
        if (StringUtils.hasText(phoneNumber)) {
            return Mono.just(new UsernamePasswordAuthenticationToken(authorizedUser, null, Collections.singletonList(new SimpleGrantedAuthority(ROLE_PREFIX + role))));
        }
        return Mono.just(authentication);
    }
}
