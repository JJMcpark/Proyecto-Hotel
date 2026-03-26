package com.proyecto.hotel.auth.controller;

import com.proyecto.hotel.auth.exception.TokenValidationException;
import com.proyecto.hotel.auth.request.LoginRequest;
import com.proyecto.hotel.auth.response.AuthResponse;
import com.proyecto.hotel.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@SecurityRequirements
@Tag(name = "Autenticación", description = "Login, logout y renovación de tokens JWT")
public class AuthController {

    private final AuthService authService;

    @Value("${application.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration; // milliseconds

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario, devuelve access token y establece refresh token en cookie HttpOnly")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.loginWithRole(request, request.getRol());
        response.addHeader(HttpHeaders.SET_COOKIE, buildRefreshCookie(authResponse.getRefreshToken()).toString());
        authResponse.setRefreshToken(null);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Genera un nuevo access token usando el refresh token de la cookie HttpOnly")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            AuthResponse authResponse = authService.refreshToken(refreshToken);
            response.addHeader(HttpHeaders.SET_COOKIE, buildRefreshCookie(authResponse.getRefreshToken()).toString());
            authResponse.setRefreshToken(null);
            return ResponseEntity.ok(authResponse);
        } catch (TokenValidationException e) {
            // Stale or revoked cookie — clear it so the browser removes it
            response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private ResponseCookie buildRefreshCookie(String value) {
        return ResponseCookie.from("refresh_token", value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .maxAge(refreshTokenExpiration / 1000)
                .path("/auth")
                .build();
    }

    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .maxAge(0)
                .path("/auth")
                .build();
    }
}
