package org.example.pharmaticb.service.auth;

import org.example.pharmaticb.Models.Request.auth.OtpRequest;
import org.example.pharmaticb.Models.Request.auth.RegistrationRequest;
import org.example.pharmaticb.Models.Request.auth.VerifyOtpRequest;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
import org.example.pharmaticb.Models.Response.auth.OtpResponse;
import org.example.pharmaticb.Models.Response.auth.VerifyOtpResponse;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


public interface RegistrationService {
    Mono<LoginResponse> registrationLogin(@Valid RegistrationRequest loginRequest, HttpHeaders httpHeaders);

    Mono<OtpResponse> sendOtp(OtpRequest request, HttpHeaders httpHeaders);

    Mono<VerifyOtpResponse> verifyOtp(VerifyOtpRequest request, HttpHeaders httpHeaders);
}
