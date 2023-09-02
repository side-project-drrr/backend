package com.drrr.auth.infrastructure.OAuth2;

public class InvalidUriException extends RuntimeException {
    public InvalidUriException(final String message) {
        super(message);
    }
}

