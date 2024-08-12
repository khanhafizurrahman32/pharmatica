package org.example.pharmaticb.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.service.security.jwt.JwtTokenHelper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    public static final String RSA_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MFswDQYJKoZIhvcNAQEBBQADSgAwRwJAW0GEOlWctT6uD3iCON/TZo71MhhebnMu\n" +
            "AS23CqAtAn3p73KdYBqMxmUb1mCTdEQ0EVjIOAMl3b3sc/TN0VGgfQIDAQAB\n" +
            "-----END PUBLIC KEY-----";

    @Bean
    public Algorithm getTokenAlgorithm() {
        return new JwtTokenHelper(RSA_PUBLIC_KEY, null).getTokenAlgorithm();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

}
