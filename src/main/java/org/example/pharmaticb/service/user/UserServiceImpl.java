package org.example.pharmaticb.service.user;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
