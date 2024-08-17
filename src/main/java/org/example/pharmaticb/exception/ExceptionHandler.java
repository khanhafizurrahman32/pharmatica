package org.example.pharmaticb.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(-2)
public class ExceptionHandler extends ReactiveExceptionHandler {
    /**
     * Create a new {@code AbstractErrorWebExceptionHandler}.
     *
     * @param errorAttributes       the error attributes
     * @param resources             the resources configuration properties
     * @param serverCodecConfigurer
     * @param applicationContext    the application context
     * @param errorResponseBuilder
     * @since 2.4.0
     */
    public ExceptionHandler(ErrorAttributes errorAttributes,
                            ApplicationContext applicationContext,
                            ServerCodecConfigurer serverCodecConfigurer,
                            ReactiveErrorResponseBuilder errorResponseBuilder) {
        super(errorAttributes, new Resources(), applicationContext, serverCodecConfigurer, errorResponseBuilder);
    }
}
