package org.example.pharmaticb.utilities;

import org.example.pharmaticb.Models.Response.ErrorResponse;
import org.example.pharmaticb.exception.InterServiceException;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public abstract class AbstractWebClient {
    private final WebClient webClient;

    protected AbstractWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> Mono<T> post(String uri, Object body, Class<T> tClass) {
        return  webClient.post()
                .uri(uri)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::getErrorResponse)
                .bodyToMono(tClass);
    }

    private <T> Mono<T> getErrorResponse(ClientResponse clientResponse) {
        if (HttpStatus.UNAUTHORIZED.equals(clientResponse.statusCode())) {
            return Mono.error(new InternalException((HttpStatus) clientResponse.statusCode(), ServiceError.USER_TOKEN_EXPIRED, "UTE_01"));
        }

        if (HttpStatus.NOT_FOUND.equals(clientResponse.statusCode())) {
            return Mono.error(new InternalException((HttpStatus) clientResponse.statusCode(), ServiceError.SERVICE_NOT_FOUND, "SNF"));
        }

        return convertToErrorResponse(clientResponse);
    }

    private <T> Mono<T> convertToErrorResponse(ClientResponse clientResponse) {
        return clientResponse
                .bodyToMono(ErrorResponse.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InternalException((HttpStatus) clientResponse.statusCode(), ServiceError.EMPTY_ERROR_RESPONSE, "EER"))))
                .flatMap(errorResponse -> Mono.error(new InterServiceException(errorResponse, clientResponse.statusCode())));

    }
}
