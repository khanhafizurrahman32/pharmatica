package org.example.pharmaticb.service.auth;

import org.example.pharmaticb.Models.Request.SmsRequest;
import org.example.pharmaticb.Models.Response.SmsResponse;
import reactor.core.publisher.Mono;

public interface SmsApiService {
    Mono<SmsResponse> sendSms(SmsRequest request);
}
