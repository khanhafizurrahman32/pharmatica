package org.example.pharmaticb.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalException extends RuntimeException {
    private HttpStatus status;
    private String message;
    private String code;

    public InternalException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
