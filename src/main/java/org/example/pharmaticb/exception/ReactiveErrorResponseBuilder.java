package org.example.pharmaticb.exception;

import org.example.pharmaticb.Models.Response.ErrorResponse;
import reactor.core.publisher.Mono;

public class ReactiveErrorResponseBuilder {
    public ReactiveErrorResponseBuilder() {
    }

    public Mono<ErrorResponse> generateErrorResponse(String code, String message) {
        return Mono.just(ErrorResponse.builder()
                .code(code)
                .message(message)
                .build()
        );
    }
}
