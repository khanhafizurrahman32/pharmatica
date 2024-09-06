package org.example.pharmaticb.utilities.Exception;

public final class ServiceError {
    private ServiceError() {}

    public static final String INVALID_REQUEST = "INVALID_REQUEST";
    public static final String WRONG_OTP = "WRONG_OTP";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String JSON_CONVERSION_ERROR = "JSON_CONVERSION_ERROR";
    public static final String USER_NOT_FOUND_ERROR = "USER_NOT_FOUND_ERROR";
    public static final String USER_TOKEN_EXPIRED = "USER_TOKEN_EXPIRED";
    public static final String SERVICE_NOT_FOUND = "SERVICE_NOT_FOUND";
    public static final String EMPTY_ERROR_RESPONSE = "EMPTY_ERROR_RESPONSE";

}
