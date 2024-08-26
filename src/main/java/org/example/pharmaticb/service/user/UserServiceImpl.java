package org.example.pharmaticb.service.user;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.UserRequest;
import org.example.pharmaticb.Models.Request.auth.RegistrationRequest;
import org.example.pharmaticb.Models.Response.UserResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.repositories.UserRepository;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Mono<User> save(RegistrationRequest request) {
        var user = User
                .builder()
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        return userRepository.save(user);
    }

    @Override
    public Flux<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .map(this::convertDbToDto);
    }

    private UserResponse convertDbToDto(User user) {
        return UserResponse.builder()
                .id(String.valueOf(user.getId()))
                .userName(user.getUserName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .build();
    }

    private User convertDtoToDb(UserRequest request, User user) {
        return User.builder()
                .id(user.getId())
                .userName(StringUtils.hasText(request.getUserName()) ? request.getUserName() : user.getUserName())
                .password(user.getPassword())
                .role(ObjectUtils.isEmpty(request.getRole()) ? user.getRole() : request.getRole())
                .email(StringUtils.hasText(request.getEmail()) ? request.getEmail() : user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(StringUtils.hasText(request.getAddress()) ? request.getAddress() : user.getAddress())
                .bloodGroup(StringUtils.hasText(request.getBloodGroup()) ? request.getBloodGroup() : user.getBloodGroup())
                .gender(StringUtils.hasText(request.getGender()) ? request.getGender() : user.getGender())
                .age(request.getAge() == 0 ? user.getAge() : request.getAge())
                .profilePictureUrl(StringUtils.hasText(request.getProfilePictureUrl()) ? request.getProfilePictureUrl() : user.getProfilePictureUrl())
                .imageUniqueId(user.getImageUniqueId())
                .build();
    }

    @Override
    public Mono<UserResponse> getUserById(Long id, AuthorizedUser authorizedUser) {
        if (id != authorizedUser.getId()) {
            return Mono.error(new InternalException(HttpStatus.FORBIDDEN, "Not allowed", ServiceError.INVALID_REQUEST));
        }
        return getUserById(id);
    }

    @Override
    public Mono<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertDbToDto);
    }

    @Override
    public Mono<UserResponse> updateUser(long id, UserRequest request) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    var updatedUser = convertDtoToDb(request, user);
                    return userRepository.save(updatedUser)
                            .map(this::convertDbToDto);
                });
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }
}
