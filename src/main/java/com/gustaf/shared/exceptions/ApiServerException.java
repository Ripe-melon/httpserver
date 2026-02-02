package com.gustaf.shared.exceptions;

public class ApiServerException extends BlogApiException {
    public ApiServerException(String message, int statusCode) {
        super(message, statusCode);
    }
}
