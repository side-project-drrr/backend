package com.example.drrrapi.web.security.exception;

public class JwtExpiredTokenException extends RuntimeException {
    public JwtExpiredTokenException(final String message) {
        super(message);
    }
}
