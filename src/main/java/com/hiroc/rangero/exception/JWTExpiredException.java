package com.hiroc.rangero.exception;

public class JWTExpiredException extends RuntimeException {
    public JWTExpiredException(String message) {
        super(message);
    }
}
