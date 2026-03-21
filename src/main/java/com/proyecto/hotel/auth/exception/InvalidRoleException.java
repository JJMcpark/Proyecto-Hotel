package com.proyecto.hotel.auth.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String message) {
        super(message);
    }
}
