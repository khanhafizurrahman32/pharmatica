package org.example.pharmaticb.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.service.security.AuthenticationManager;
import org.example.pharmaticb.service.security.jwt.ReactiveJWTTokenAuthenticationFilter;
import org.example.pharmaticb.service.security.SecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final List<String> openApis = List.of("/actuator/health", "/api/reg/login", "/api/orders");
    @Bean
    public AuthenticationManager authenticationManager(Algorithm tokenAlgorithm) {
        return new AuthenticationManager(tokenAlgorithm);
    }

    @PostConstruct
    public void init() {
        log.info("Initializing security");
    }

    @Bean
    public SecurityContextRepository  securityContextRepository(AuthenticationManager authenticationManager) {
        return new SecurityContextRepository(authenticationManager);
    }

    @Bean
    public ReactiveJWTTokenAuthenticationFilter authenticationFilter() {
        var filter = new ReactiveJWTTokenAuthenticationFilter();
        filter.setWhiteList(openApis);
        return filter;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            AuthenticationManager authenticationManager,
                                                            SecurityContextRepository securityContextRepository) {
        String[] openApisArray = openApis.toArray(new String[0]);
        Arrays.stream(openApisArray).forEach(log::info);
        return http
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange((exchanges) ->
                        exchanges.pathMatchers(openApisArray ).permitAll()
                                .pathMatchers("/admin/**").hasRole("ADMIN")
                                .pathMatchers("/customer/**").hasRole("USER")
                                .anyExchange()
                                .authenticated()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .exceptionHandling(exceptionHandlingSpec -> {
                    exceptionHandlingSpec.authenticationEntryPoint((swe, e) ->
                            Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)));
                    exceptionHandlingSpec.accessDeniedHandler((swe, e) ->
                            Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)));
                })
                .build();
    }
}
