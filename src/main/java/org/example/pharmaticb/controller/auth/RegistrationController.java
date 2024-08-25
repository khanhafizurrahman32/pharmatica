package org.example.pharmaticb.controller.auth;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.auth.OtpRequest;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Request.auth.RegistrationRequest;
import org.example.pharmaticb.Models.Request.auth.VerifyOtpRequest;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.example.pharmaticb.Models.Response.auth.OtpResponse;
import org.example.pharmaticb.Models.Response.auth.VerifyOtpResponse;
import org.example.pharmaticb.controller.BaseController;
import org.example.pharmaticb.service.auth.RegistrationService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@BaseController
@RestController
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/reg/login")
    public Mono<LoginResponse> registrationLogin(@Valid @RequestBody RegistrationRequest request, @RequestHeader HttpHeaders httpHeaders) {
        return registrationService.registrationLogin(request, httpHeaders);
    }

    @PostMapping("/otp/send")
    public Mono<OtpResponse> sendOtp(@Valid @RequestBody OtpRequest request, @RequestHeader HttpHeaders httpHeaders) {
        return registrationService.sendOtp(request, httpHeaders);
    }

    @PostMapping("/otp/verify")
    public Mono<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request, @RequestHeader HttpHeaders httpHeaders) {
        return registrationService.verifyOtp(request, httpHeaders);
    }

}
