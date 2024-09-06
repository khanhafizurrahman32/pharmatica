package org.example.pharmaticb.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class InternalException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    private final String code;

    public InternalException(HttpStatus status, String message, String code) {
        super(message);
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
