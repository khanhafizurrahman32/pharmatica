package org.example.pharmaticb.exception;

import org.apache.http.HttpStatus;
import org.example.pharmaticb.Models.Response.ErrorResponse;
import org.springframework.http.HttpStatusCode;

public class InterServiceException extends RuntimeException {
    private HttpStatusCode code;
    private ErrorResponse errorResponse;

    public InterServiceException(ErrorResponse errorResponse, HttpStatusCode code) {
        super(errorResponse.getMessage());
        this.code = code;
        this.errorResponse = errorResponse;
    }
}
