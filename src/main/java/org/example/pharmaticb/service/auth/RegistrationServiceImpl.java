package org.example.pharmaticb.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.auth.OtpRequest;
import org.example.pharmaticb.Models.Request.auth.RegistrationRequest;
import org.example.pharmaticb.Models.Request.auth.UserStatusRequest;
import org.example.pharmaticb.Models.Request.auth.VerifyOtpRequest;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.example.pharmaticb.Models.Response.auth.OtpResponse;
import org.example.pharmaticb.Models.Response.auth.UserStatusResponse;
import org.example.pharmaticb.Models.Response.auth.VerifyOtpResponse;
import org.example.pharmaticb.dto.enums.Role;
import org.example.pharmaticb.dto.enums.UserStatus;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.repositories.UserRepository;
import org.example.pharmaticb.service.user.UserService;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.example.pharmaticb.utilities.ProfileConstants;
import org.example.pharmaticb.utilities.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

import static org.example.pharmaticb.dto.enums.UserStatus.NOT_REGISTERED;
import static org.example.pharmaticb.dto.enums.UserStatus.OTP_VERIFIED;


@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    public static final String USER_IS_NOT_ELIGIBLE_FOR_GETTING_OTP = "User is not eligible for getting Otp";
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.profile}")
    private String profile;


    @Override
    public Mono<LoginResponse> registrationLogin(@Valid RegistrationRequest registrationRequest, HttpHeaders httpHeaders) {
        return findByPhoneNumber(registrationRequest.getPhoneNumber())
                .flatMap(existingUser -> {
                    if (OTP_VERIFIED.name().equals(existingUser.getRegistrationStatus())) {
                        return getLoginResponseMono(registrationRequest, existingUser);
                    }
                    if (NOT_REGISTERED.name().equals(existingUser.getRegistrationStatus())) {
                        return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "User is not verified through OTP", ServiceError.INVALID_REQUEST));
                    }
                    return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "User already Registered", ServiceError.INVALID_REQUEST));
                })
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "User is not found", ServiceError.INVALID_REQUEST))));
    }

    @Override
    public Mono<OtpResponse> sendOtp(OtpRequest request, HttpHeaders httpHeaders) {
        return userRepository.findByPhoneNumber(request.getPhoneNumber())
                .filter(this::validateOtpRequest)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InternalException(HttpStatus.BAD_REQUEST, ServiceError.INVALID_REQUEST, USER_IS_NOT_ELIGIBLE_FOR_GETTING_OTP))))
                .flatMap(user -> {
                    var otpCode = getOtpCode();
//                    var otpHash = generateHash(otpCode);

                    var smsContent = String.format(Utility.SMS_CONTENT, otpCode);

                    log.info("Final SMS Content: {}", smsContent);

                    sendOtp(request, smsContent);
                    UpdateUserData(otpCode, user);
                    return userRepository.save(user)
                            .thenReturn(OtpResponse.builder().build());
                });
    }

    private void UpdateUserData(String otpCode, User user) {
        user.setOtpExpirationTime(getOtpExpirationTime());
        user.setOtpCode(Base64.getEncoder().encodeToString(otpCode.getBytes(StandardCharsets.UTF_8)));
    }

    private long getOtpExpirationTime() {
        var aa = System.currentTimeMillis() + Utility.otpExpirationInSecond() * Utility.ONE_SECOND_IN_MILLIS;
        return aa;
    }

    @Override
    public Mono<VerifyOtpResponse> verifyOtp(VerifyOtpRequest request, HttpHeaders httpHeaders) {
        return userRepository.findByPhoneNumber(request.getPhoneNumber())
                .filter(this::validateOtpRequest)
                .flatMap(user -> verifyOtp(request, user)
                        .flatMap(verifyOtpResponse -> updateUserStatus(user).map(u -> verifyOtpResponse)))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Wrong otp", ServiceError.WRONG_OTP))));
    }

    private Mono<User> updateUserStatus(User user) {
        user.setRegistrationStatus(OTP_VERIFIED.name());
        return userRepository.save(user);
    }

    @Override
    public Mono<UserStatusResponse> getUserStatus(UserStatusRequest request, HttpHeaders httpHeaders) {
        return userRepository.findByPhoneNumber(request.getPhoneNumber())
                .map(user -> UserStatusResponse.builder().status(user.getRegistrationStatus()).build())
                .switchIfEmpty(Mono.defer(() -> userRepository.save(User.builder()
                                .phoneNumber(request.getPhoneNumber())
                                .registrationStatus(UserStatus.NOT_REGISTERED.name())
                                .build())
                        .map(user -> UserStatusResponse.builder().status(user.getRegistrationStatus()).build())));
    }

    private Mono<VerifyOtpResponse> verifyOtp(VerifyOtpRequest request, User user) {
        if (System.currentTimeMillis() > user.getOtpExpirationTime()) {
            log.info("current time {} :: otpExpirationTime {}", System.currentTimeMillis(), user.getOtpExpirationTime());
            throw new InternalException(HttpStatus.INTERNAL_SERVER_ERROR, "OTP time exceeded", ServiceError.INVALID_REQUEST);
        }

        byte[] decodedBytes = Base64.getDecoder().decode(user.getOtpCode().getBytes());
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        if (Objects.equals(request.getOtpCode(), decodedString)) {
            return getVerifiedOtpResponse(user);
        }

        return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Wrong otp", ServiceError.WRONG_OTP));
    }

    private Mono<VerifyOtpResponse> getVerifiedOtpResponse(User user) {
        var pinNonce = getPinNonce();
        updateUserTable(user, pinNonce);

        return userRepository.save(user)
                .map(ret -> VerifyOtpResponse.builder()
                        .pinNonce(pinNonce)
                        .build());
    }

    private void updateUserTable(User user, String pinNonce) {
        user.setOtpStatus(true);
        user.setPinNonce(pinNonce);

    }

    private String getPinNonce() {
        return Long.toString(System.nanoTime());
    }

    private boolean validateOtpRequest(User user) {
        var aa = NOT_REGISTERED.name().equals(user.getRegistrationStatus())
                && !user.isOtpStatus();
        return aa;
    }

    private void sendOtp(OtpRequest request, String smsContent) {
        // Todo: integrate sms gateway
    }

    private String getOtpCode() {
        if (ProfileConstants.PROFILE_DEV.equals(profile) || ProfileConstants.PROFILE_SIT.equals(profile)) {
            return "123456";
        }
        return getOtpCode(6, "0123456789");//todo: cms
    }

//    public static String generateHash(String otpCode) {
//        var otpSmsHourMinute = DateUtil.getOtpSmsHourMinute();
//
//        var rightShift = rightShift(otpSmsHourMinute + otpCode, 4);
//        var base64Data = Base64.getEncoder().encodeToString(rightShift.getBytes(StandardCharsets.UTF_8));
//
//        return leftShift(base64Data, 4);
//    }

    private static String leftShift(String data, int position) {
        if (data.length() <= position) return data;

        StringBuilder stringBuilder = new StringBuilder();

        String firstPart = data.substring(position);
        String lastPart = data.substring(0, position);

        stringBuilder.append(firstPart).append(lastPart);

        return stringBuilder.toString();
    }

    public static String rightShift(String data, int position) {
        if (data.length() <= position) return data;

        StringBuilder stringBuilder = new StringBuilder();

        String firstPart = data.substring(data.length() - position);
        String lastPart = data.substring(0, data.length() - position);

        stringBuilder.append(firstPart).append(lastPart);

        return stringBuilder.toString();
    }

    private String getOtpCode(int length, String possible) {
        StringBuilder otpCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otpCode.append(possible.charAt((int) Math.floor(this.secureRandom.nextDouble() * possible.length())));
        }
        return otpCode.toString();
    }

    private Mono<LoginResponse> getLoginResponseMono(RegistrationRequest request, User existingUser) {
        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        existingUser.setRegistrationStatus(UserStatus.REGISTERED.name());
        existingUser.setRole(request.getRole());
        return userService.save(existingUser)
                .map(user -> LoginResponse.builder()
                        .accessToken(jwtTokenService.generateAccessToken(user, ObjectUtils.isEmpty(request.getRole()) ? Role.USER.name() : request.getRole().name()))
                        .refreshToken(jwtTokenService.generateRefreshToken(user, ObjectUtils.isEmpty(request.getRole()) ? Role.USER.name() : request.getRole().name()))
                        .accessExpiredIn(jwtTokenService.getAccessExpiredTime())
                        .refreshExpiredIn(jwtTokenService.getRefreshExpiredTime())
                        .build());
    }

    private Mono<User> findByPhoneNumber(String phoneNumber) {
        return userService.findByPhoneNumber(phoneNumber);
    }
}
