package org.example.pharmaticb.repositories.auth;

import org.example.pharmaticb.Models.DB.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByCustomerName(String customerName);
}
