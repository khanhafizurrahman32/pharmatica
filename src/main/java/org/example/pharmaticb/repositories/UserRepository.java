package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByPhoneNumber(String phoneNumber);
}
