package org.example.pharmaticb.service.user;

import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> findByCustomerName(String customerName);
    Mono<User> save(LoginRequest request);
}
