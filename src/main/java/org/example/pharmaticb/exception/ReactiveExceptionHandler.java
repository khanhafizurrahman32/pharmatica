package org.example.pharmaticb.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.example.pharmaticb.Models.Response.ErrorResponse;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class ReactiveExceptionHandler extends AbstractErrorWebExceptionHandler {
    private final ReactiveErrorResponseBuilder errorResponseBuilder;
    /**
     * Create a new {@code AbstractErrorWebExceptionHandler}.
     *
     * @param errorAttributes    the error attributes
     * @param resources          the resources configuration properties
     * @param applicationContext the application context
     * @since 2.4.0
     */
    public ReactiveExceptionHandler(ErrorAttributes errorAttributes,
                                    WebProperties.Resources resources,
                                    ApplicationContext applicationContext,
                                    ServerCodecConfigurer serverCodecConfigurer,
                                    ReactiveErrorResponseBuilder errorResponseBuilder) {
        super(errorAttributes, resources, applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());

        this.errorResponseBuilder = errorResponseBuilder;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorServerResponse);
    }

    private Mono<ServerResponse> renderErrorServerResponse(ServerRequest serverRequest) {
        Throwable throwable = getError(serverRequest);

        return generateErrorResponse(throwable)
                .flatMap(httpStatusErrorResponsePair -> createErrorServerResponse(httpStatusErrorResponsePair.getLeft(),
                        httpStatusErrorResponsePair.getRight()));
    }

    private Mono<ServerResponse> createErrorServerResponse(HttpStatus httpStatus, ErrorResponse errorResponse) {
        if (Objects.isNull(errorResponse)) {
            return ServerResponse.status(httpStatus)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }

    private Mono<Pair<HttpStatus, ErrorResponse>> generateErrorResponse(Throwable throwable) {

        Throwable rootCause = ExceptionUtils.getRootCause(throwable);

        if (throwable instanceof InternalException internalException) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message(internalException.getMessage())
                    .code(internalException.getCode())
                    .build();

            return Mono.just(Pair.of(internalException.getStatus(), errorResponse));

        } else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .code(ServiceError.UNKNOWN)
                    .message("An error occurred!!")
                    .build();
            return Mono.just(Pair.of(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse));
        }
    }
}
