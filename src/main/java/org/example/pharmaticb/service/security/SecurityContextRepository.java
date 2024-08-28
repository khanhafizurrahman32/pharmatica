package org.example.pharmaticb.service.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.service.auth.JwtTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.example.pharmaticb.utilities.SecurityUtil.TOKEN_PREFIX;
import static org.example.pharmaticb.utilities.SecurityUtil.TOKEN_ROLE;
import static org.example.pharmaticb.utilities.Utility.ROLE_PREFIX;

@Slf4j
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private final AuthenticationManager authenticationManager;
    private final List<String> openApis;
    private final JwtTokenService jwtTokenService;

    public SecurityContextRepository(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.openApis = new ArrayList<>();
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX) && !openApis.contains(extractApiPath(exchange))) {
            String token = authHeader.substring(7);
            DecodedJWT jwt = jwtTokenService.getDecodedJwtToken(token);
            String role = jwt.getClaim(TOKEN_ROLE).asString();
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(ROLE_PREFIX+ role));

            var usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(token, token, authorities);

            return this.authenticationManager.authenticate(usernamePasswordAuthentication)
                    .map(SecurityContextImpl::new);
        }
        return Mono.empty();
    }

    private String extractApiPath(ServerWebExchange exchange) {
        RequestPath path = exchange.getRequest().getPath();
        return path.value().replace(path.contextPath().value(), "");
    }
}
