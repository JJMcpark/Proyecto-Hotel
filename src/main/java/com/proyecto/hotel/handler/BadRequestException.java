package com.proyecto.hotel.handler;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
