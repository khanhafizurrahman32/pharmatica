package org.example.pharmaticb.service.user;

import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.CategoryRequest;
import org.example.pharmaticb.Models.Request.UserRequest;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Response.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

public interface UserService {
    Mono<User> findByCustomerName(String customerName);

    Mono<User> save(LoginRequest request);

    Flux<UserResponse> getAllUsers();

    Mono<UserResponse> getUserById(Long id);

    Mono<UserResponse> updateUser(@Valid long id, @Valid UserRequest request);

    Mono<Void> deleteUser(Long id);
}
