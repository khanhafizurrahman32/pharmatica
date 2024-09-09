package org.example.pharmaticb.service.user;

import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.UserRequest;
import org.example.pharmaticb.Models.Response.UserResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import javax.validation.Valid;

public interface UserService {
    Mono<User> findByPhoneNumber(String phoneNumber);

    Mono<User> save(User user);

    Flux<UserResponse> getAllUsers();

    Mono<UserResponse> getUserById(Long id, AuthorizedUser authorizedUser);

    Mono<UserResponse> getUserByPhoneNumber(String phoneNumber, AuthorizedUser authorizedUser);

    Mono<UserResponse> getUserById(Long id);

    Mono<UserResponse> updateUser(@Valid long id, @Valid UserRequest request);

    Mono<Void> deleteUser(Long id);
}
