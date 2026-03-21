package com.proyecto.hotel.auth.exception;

public class TooManyLoginAttemptsException extends RuntimeException {
    public TooManyLoginAttemptsException(String message) {
        super(message);
    }
}
