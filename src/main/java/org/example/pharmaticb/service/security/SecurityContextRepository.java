package org.example.pharmaticb.service.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.example.pharmaticb.utilities.SecurityUtil.TOKEN_PREFIX;

@Slf4j
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private final AuthenticationManager authenticationManager;
    private final List<String> openApis;

    public SecurityContextRepository(AuthenticationManager authenticationManager, List<String> openApis) {
        this.authenticationManager = authenticationManager;
        this.openApis = openApis;
    }

    public SecurityContextRepository(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
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
            var usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(token, token);

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
