package org.example.pharmaticb.service.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.RefreshTokenRequest;
import org.example.pharmaticb.Models.Request.SmsRequest;
import org.example.pharmaticb.Models.Request.auth.ForgetPasswordRequest;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Request.auth.OtpRequest;
import org.example.pharmaticb.Models.Request.auth.UpdatePasswordRequest;
import org.example.pharmaticb.Models.Response.auth.ForgetPasswordResponse;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.example.pharmaticb.Models.Response.auth.RefreshTokenResponse;
import org.example.pharmaticb.Models.Response.auth.UpdatePasswordResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.repositories.UserRepository;
import org.example.pharmaticb.service.user.UserService;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.example.pharmaticb.utilities.Utility;
import org.example.pharmaticb.utilities.log.Loggable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;

import static org.example.pharmaticb.utilities.Utility.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final SmsApiService smsApiService;

    @Override
    @Loggable
    public Mono<LoginResponse> login(LoginRequest request, HttpHeaders httpHeaders) {
        return userService.findByPhoneNumber(request.getPhoneNumber())
                .filter(userDetails -> passwordEncoder.matches(request.getPassword(), userDetails.getPassword()))
                .switchIfEmpty(Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Username or Password is incorrect", ServiceError.INVALID_REQUEST)))
                .filter(user -> !user.isDeactivated())
                .switchIfEmpty(Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "User is deactivated", ServiceError.DEACTIVATED_USER)))
                .map(userDetails -> LoginResponse.builder()
                        .accessToken(jwtTokenService.generateAccessToken(userDetails, userDetails.getRole().name()))
                        .refreshToken(jwtTokenService.generateRefreshToken(userDetails, userDetails.getRole().name()))
                        .accessExpiredIn(jwtTokenService.getAccessExpiredTime())
                        .refreshExpiredIn(jwtTokenService.getRefreshExpiredTime())
                        .build());
    }

    @Override
    @Loggable
    public Mono<UpdatePasswordResponse> updatePassword(UpdatePasswordRequest request, AuthorizedUser authorizedUser, HttpHeaders httpHeaders) {
        return userRepository.findById(authorizedUser.getId())
                .flatMap(user -> {
                    if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                        return userRepository.save(user)
                                .thenReturn(UpdatePasswordResponse.builder().success(true).build());
                    }
                    return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Password does not match", ServiceError.INVALID_REQUEST));
                })
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "User does not exist", ServiceError.INVALID_REQUEST))));
    }

    @Override
    @Loggable
    public Mono<ForgetPasswordResponse> forgetPassword(ForgetPasswordRequest request, HttpHeaders httpHeaders) {
        return userService.findByPhoneNumber(request.getPhoneNumber())
                .flatMap(user -> sendOtp(request.getPhoneNumber(), user)
                        .map(tempPassword -> ForgetPasswordResponse.builder().tempPassword(tempPassword).build()))
                .switchIfEmpty(Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "User does not exist", ServiceError.INVALID_REQUEST)));
    }

    @Override
    @Loggable
    public Mono<RefreshTokenResponse> refreshToken(RefreshTokenRequest request) {
        DecodedJWT jwt = jwtTokenService.getDecodedJwtToken(request.getRefreshToken());
        String phoneNumber = jwt.getAudience().get(0);
        return userService.findByPhoneNumber(phoneNumber).map(user -> RefreshTokenResponse.builder()
                .accessToken(jwtTokenService.generateAccessToken(user, user.getRole().name()))
                .refreshToken(jwtTokenService.generateRefreshToken(user, user.getRole().name()))
                .accessExpiredIn(jwtTokenService.getAccessExpiredTime())
                .refreshExpiredIn(jwtTokenService.getRefreshExpiredTime())
                .build());
    }

    private Mono<String> sendOtp(String phoneNumber, User user) {
        String tempPassword = getTempPassword();
        var smsContent = String.format(Utility.SMS_CONTENT, tempPassword);

        return sendOtp(phoneNumber, smsContent).then(Mono.defer(() -> {
            updateUserPassword(tempPassword, user);
            return userRepository.save(user).thenReturn(tempPassword);
        }));
    }

    private void updateUserPassword(String tempPassword, User user) {
        user.setPassword(passwordEncoder.encode(tempPassword));
    }

    private String getTempPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Add one character from each required type
        password.append(LOWER_CHARS.charAt(random.nextInt(LOWER_CHARS.length())));
        password.append(UPPER_CHARS.charAt(random.nextInt(UPPER_CHARS.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Add random characters to reach minimum length of 6
        String allChars = LOWER_CHARS + UPPER_CHARS + NUMBERS + SPECIAL_CHARS;
        while (password.length() < 6) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = passwordArray[index];
            passwordArray[index] = passwordArray[i];
            passwordArray[i] = temp;
        }

        return new String(passwordArray);
    }

    private Mono<Void> sendOtp(String phoneNumber, String smsContent) {
        return smsApiService.sendSms(SmsRequest.builder()
                        .number(phoneNumber)
                        .message(smsContent)
                        .build())
                .doOnNext(smsResponse -> {
                    if (StringUtils.hasText(smsResponse.getErrorMessage())) {
                        log.error("sms sending error:: code {} | message {}", smsResponse.getResponseCode(), smsResponse.getErrorMessage());
                    } else {
                        log.info("sms response code {} | message {}", smsResponse.getResponseCode(), smsResponse.getSuccessMessage());
                    }
                })
                .then();
    }
}
