package org.example.pharmaticb.service.user;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.CategoryRequest;
import org.example.pharmaticb.Models.Request.UserRequest;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Response.UserResponse;
import org.example.pharmaticb.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    @Override
    public Mono<User> findByCustomerName(String customerName) {
        return userRepository.findByCustomerName(customerName);
    }

    @Override
    public Mono<User> save(LoginRequest request) {
        var user = User
                .builder()
                .customerName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        return userRepository.save(user);
    }

    @Override
    public Flux<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .map(user -> mapper.map(user, UserResponse.class));
    }

    @Override
    public Mono<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> mapper.map(user, UserResponse.class));
    }

    @Override
    public Mono<UserResponse> updateUser(long id, UserRequest request) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    mapper.map(request, user);
                    return userRepository.save(user)
                            .map(updateUser -> mapper.map(updateUser, UserResponse.class));
                });
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }
}
