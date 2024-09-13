package org.example.pharmaticb.service.email;

import reactor.core.publisher.Mono;

public interface EmailService {
    Mono<String> sendEmail(String to, String subject, String text);
}
