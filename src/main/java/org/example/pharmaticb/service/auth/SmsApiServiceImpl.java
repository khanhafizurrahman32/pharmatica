package org.example.pharmaticb.service.auth;

import org.example.pharmaticb.Models.Request.SmsRequest;
import org.example.pharmaticb.Models.Response.SmsResponse;
import org.example.pharmaticb.utilities.AbstractWebClient;
import org.example.pharmaticb.utilities.log.Loggable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SmsApiServiceImpl extends AbstractWebClient implements SmsApiService {
    private static final String SMS_ENDPOINT = "/smsapi";

    protected SmsApiServiceImpl(@Qualifier("smsWebClient") WebClient webClient) {
        super(webClient);
    }

    @Override
    @Loggable
    public Mono<SmsResponse> sendSms(SmsRequest request) {
        return post(SMS_ENDPOINT, request, SmsResponse.class);
    }
}
