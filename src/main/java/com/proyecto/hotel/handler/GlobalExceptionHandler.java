package com.proyecto.hotel.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de credenciales incorrectas
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.warn("BadCredentialsException: {}", e.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("DNI o contraseña incorrectos")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja excepciones de usuario no encontrado
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException e, HttpServletRequest request) {
        log.warn("UsernameNotFoundException: {}", e.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Usuario no encontrado")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja excepciones de acceso denegado (falta de permisos)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("AccessDeniedException: {}", e.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("No tienes permisos para acceder a este recurso")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({com.proyecto.hotel.auth.exception.InvalidRoleException.class})
    public ResponseEntity<ApiError> handleInvalidRoleException(RuntimeException e, HttpServletRequest request) {
        log.warn("InvalidRoleException: {}", e.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({com.proyecto.hotel.auth.exception.TooManyLoginAttemptsException.class})
    public ResponseEntity<ApiError> handleTooManyAttempts(RuntimeException e, HttpServletRequest request) {
        log.warn("TooManyLoginAttemptsException: {}", e.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error("Too Many Requests")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler({com.proyecto.hotel.auth.exception.TokenValidationException.class})
    public ResponseEntity<ApiError> handleTokenValidation(RuntimeException e, HttpServletRequest request) {
        log.warn("TokenValidationException: {}", e.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja excepciones de validación de argumentos (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación");
        
        log.warn("Validation error: {}", message);
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de argumentos inválidos
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("IllegalArgumentException: {}", e.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException e, HttpServletRequest request) {
        log.warn("BadRequestException: {}", e.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones cuando no se encuentra un elemento
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNoSuchElementException(NoSuchElementException e, HttpServletRequest request) {
        log.warn("NoSuchElementException: {}", e.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("El recurso solicitado no existe")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de entidad no encontrada (JPA)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        log.warn("EntityNotFoundException: {}", e.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones genéricas de runtime
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("RuntimeException: ", e);
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Error interno del servidor")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
