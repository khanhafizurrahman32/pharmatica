package org.example.pharmaticb.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.exception.ReactiveErrorResponseBuilder;
import org.example.pharmaticb.service.security.jwt.JwtTokenHelper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.security.SecureRandom;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    public static final String RSA_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAps8lE2s6P6PZoTwT/nKs+QvvYOasr8LpgsHQdk4ghYE/vQKjEB9n5lxzHzmaIoxJcoiGU+QZuEB6MP9rovd4gIwKoQAfpEbdoondBXro2RzNzu+vQbmwTs5zr2pw5oXVYGfW8lt+d2sh5kkaGEoAk0bRe1vBlnbVbd/02QNncRsUhHtaTONhVGuU5vBJ/W38ub4beD9mP/rgZosSi+1qR7u5HmWcbwKD+tc59ZdRIfuDofTPjZMQak5VRHiWsLpyqV45ccq67MFqfCKLAXyRt0SDztwALdwXhhhRU+jenAtm+6jP8Jsf/wTmH7Zf7jf5kjNC4jbEzVuz82uZX8VmAE8B1I7bQkUJCjnmwnfyglbVEuoG9n4QGEjqvx1XgKtJ19rYwjqGfDhIri8rrvx1gc1WxzfLSE1RfbHQDv3hK1L/bdacI2HrvTaK0sgf8go5gS8pKutbusLpshpW5ki/sEq4QfMJ/AqMLOlZfEKukUWs+ISlw+0j9MwNIwSseJuZCiyenGTwZrlniDJMMFVpHXLa6wjxAshdh4BQ+Ksdfj95GYf7WjtibxVfHRWMlWOM4skAZ2VKBzxVNf6f5G/XSroTojGQnXPJMkk+YaWSZxkQTDfAm7XuzM7dPLxZwcM++HPprGApe4UWKuyZD/KxIFfT9edwqRPo4wG0C4JUZxECAwEAAQ==-----END PUBLIC KEY-----";

    public static final String RSA_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----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-----END PRIVATE KEY-----";

    @Bean
    public Algorithm getTokenAlgorithm() {
        return new JwtTokenHelper(RSA_PUBLIC_KEY, RSA_PRIVATE_KEY).getTokenAlgorithm();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ReactiveErrorResponseBuilder reactiveErrorResponseBuilder() {
        return new ReactiveErrorResponseBuilder();
    }

    @Bean
    SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Bean
    public WebFluxConfigurer webFluxConfigurer() {
        return new WebFluxConfigurer() {
            @Override
            public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
                configurer.defaultCodecs().maxInMemorySize(1024 * 1024); // 1MB
            }
        };
    }

}
