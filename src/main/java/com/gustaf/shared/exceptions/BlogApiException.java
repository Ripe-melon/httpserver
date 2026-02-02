package com.gustaf.shared.exceptions;

public class BlogApiException extends RuntimeException {
    private final int statusCode;

    public BlogApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
