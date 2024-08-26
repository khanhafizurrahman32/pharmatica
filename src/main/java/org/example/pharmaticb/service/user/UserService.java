package org.example.pharmaticb.service.user;

import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.UserRequest;
import org.example.pharmaticb.Models.Request.auth.RegistrationRequest;
import org.example.pharmaticb.Models.Response.UserResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

public interface UserService {
    Mono<User> findByPhoneNumber(String phoneNumber);

    Mono<User> save(RegistrationRequest request);

    Flux<UserResponse> getAllUsers();

    Mono<UserResponse> getUserById(Long id, AuthorizedUser authorizedUser);

    Mono<UserResponse> getUserById(Long id);

    Mono<UserResponse> updateUser(@Valid long id, @Valid UserRequest request);

    Mono<Void> deleteUser(Long id);
}
