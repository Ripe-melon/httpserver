package com.gustaf.shared.exceptions;

public class ResourceNotFoundException extends BlogApiException {
    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}
