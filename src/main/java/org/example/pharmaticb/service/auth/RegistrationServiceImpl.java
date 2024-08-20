package org.example.pharmaticb.service.auth;

import com.google.common.base.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.User;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Request.auth.OtpRequest;
import org.example.pharmaticb.Models.Request.auth.VerifyOtpRequest;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.example.pharmaticb.Models.Response.auth.OtpResponse;
import org.example.pharmaticb.Models.Response.auth.VerifyOtpResponse;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.repositories.UserRepository;
import org.example.pharmaticb.service.user.UserService;
import org.example.pharmaticb.utilities.DateUtil;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.example.pharmaticb.utilities.ProfileConstants;
import org.example.pharmaticb.utilities.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;


@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    public static final String USER_IS_NOT_ELIGIBLE_FOR_GETTING_OTP = "User is not eligible for getting Otp";
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom;
    @Value("${app.profile}")
    private String profile;


    @Override
    public Mono<LoginResponse> registrationLogin(LoginRequest loginRequest, HttpHeaders httpHeaders) {
        return findByCustomerName(loginRequest.getUserName())
                .flatMap(existingUser -> Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "User already exists", ServiceError.INVALID_REQUEST)))
                .switchIfEmpty(Mono.defer(() -> getLoginResponseMono(loginRequest)))
                .cast(LoginResponse.class);
    }

    @Override
    public Mono<OtpResponse> sendOtp(OtpRequest request, HttpHeaders httpHeaders) {
        return userRepository.findById(Long.valueOf(request.getPhone()))
                .filter(user -> validateOtpRequest(request, user))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InternalException(HttpStatus.BAD_REQUEST, ServiceError.INVALID_REQUEST, USER_IS_NOT_ELIGIBLE_FOR_GETTING_OTP))))
                .map(user -> {
                    var otpCode = getOtpCode();
                    var otpHash = generateHash(otpCode);

                    var smsContent = String.format("otpsms", otpCode, otpHash);

                    log.info("Final SMS Content: {}", smsContent);

                    sendOtp(request, smsContent);
                    return OtpResponse.builder().build();
                });
    }

    @Override
    public Mono<VerifyOtpResponse> verifyOtp(VerifyOtpRequest request, HttpHeaders httpHeaders) {
        return userRepository.findById(Long.valueOf(request.getPhone()))
                .filter(user -> validateOtpRequest(request, user))
                .flatMap(user -> verifyOtp(request, user))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Wrong otp", ServiceError.WRONG_OTP))));
    }

    private Mono<VerifyOtpResponse> verifyOtp(VerifyOtpRequest request, User user) {
        if (System.currentTimeMillis() > user.getOtpExpirationTime()) {
            throw new InternalException(HttpStatus.INTERNAL_SERVER_ERROR, "OTP time exceeded", ServiceError.INVALID_REQUEST);
        }

        if (Objects.equal(user.getOtpCode(), Base64.getEncoder().encodeToString(request.getOtpCode().getBytes()))) {
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

    private boolean validateOtpRequest(VerifyOtpRequest request, User user) {
        return Utility.INITIATED.equals(user.getRegistrationStatus())
                && !user.isOtpStatus();
    }

    private void sendOtp(OtpRequest request, String smsContent) {
        // Todo: integrate sms gateway
    }

    private String getOtpCode() {
        if (ProfileConstants.PROFILE_DEV.equals(profile) || ProfileConstants.PROFILE_SIT.equals(profile)) {
            return "123456";
        }
        return getOtpCode(6, "content");//todo: cms
    }

    public static String generateHash(String otpCode) {
        var otpSmsHourMinute = DateUtil.getOtpSmsHourMinute();

        var rightShift = rightShift(otpSmsHourMinute + otpCode, 4);
        var base64Data = Base64.getEncoder().encodeToString(rightShift.getBytes(StandardCharsets.UTF_8));

        return leftShift(base64Data, 4);
    }

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

    private boolean validateOtpRequest(OtpRequest request, User user) {
//        return Utility.INITIATED.equals(user.getPhone()) && !user.isOtpStatus();
        return false;
    }

    private Mono<LoginResponse> getLoginResponseMono(LoginRequest request) {
        return userService.save(request)
                .map(user -> LoginResponse.builder()
                        .accessToken(jwtTokenService.generateAccessToken(user, request))
                        .refreshToken(jwtTokenService.generateRefreshToken(user, request))
                        .accessExpiredIn(jwtTokenService.getAccessExpiredTime())
                        .refreshExpiredIn(jwtTokenService.getRefreshExpiredTime())
                        .build());
    }

    private Mono<User> findByCustomerName(String customerName) {
        return userService.findByCustomerName(customerName);
    }
}
