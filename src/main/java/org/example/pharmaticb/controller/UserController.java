package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.CategoryRequest;
import org.example.pharmaticb.Models.Request.UserRequest;
import org.example.pharmaticb.Models.Response.UserResponse;
import org.example.pharmaticb.service.user.UserService;
import org.example.pharmaticb.utilities.Utility;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.security.Principal;

@BaseController
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public Flux<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public Mono<UserResponse> getUserById(@PathVariable Long id, Principal principal) {
        return userService.getUserById(id, Utility.extractAuthorizedUserFromPrincipal(principal));
    }

    @GetMapping("/users/{phoneNumber}")
    public Mono<UserResponse> getUserByPhoneNumber(@PathVariable String phoneNumber, Principal principal) {
        return userService.getUserByPhoneNumber(phoneNumber, Utility.extractAuthorizedUserFromPrincipal(principal));
    }

    @PutMapping("/users/{id}")
    public Mono<UserResponse> updateUser(@Valid @PathVariable long id, @Valid @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/users/{id}")
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
