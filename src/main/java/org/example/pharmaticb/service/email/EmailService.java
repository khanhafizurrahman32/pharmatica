package org.example.pharmaticb.service.email;

import reactor.core.publisher.Mono;

public interface EmailService {
    String sendEmail(String to, String subject, String text);
}
