package org.example.pharmaticb.controller.auth;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.auth.LoginRequest;
import org.example.pharmaticb.Models.Response.auth.LoginResponse;
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
    public Mono<LoginResponse> registrationLogin(@Valid @RequestBody LoginRequest request, @RequestHeader HttpHeaders httpHeaders) {
        return registrationService.registrationLogin(request, httpHeaders);
    }
}
