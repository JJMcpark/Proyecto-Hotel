package com.proyecto.hotel.auth.exception;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String message) {
        super(message);
    }
}
