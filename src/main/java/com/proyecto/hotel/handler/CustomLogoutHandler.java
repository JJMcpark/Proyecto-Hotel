package com.proyecto.hotel.handler;

import com.proyecto.hotel.model.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, 
                       HttpServletResponse response, 
                       Authentication authentication) {
        
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Validar que exista header Authorization con token Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Logout attempted without valid Authorization header");
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }

        // Extraer token del header (remover "Bearer " del inicio)
        String token = authHeader.substring(7);

        // Buscar el token en la base de datos
        var storedToken = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));

        // Marcar el token como logged out (revocado)
        if (storedToken != null && !storedToken.getIsLoggedOut()) {
            storedToken.setIsLoggedOut(true);
            refreshTokenRepository.save(storedToken);
            log.info("Token revoked for user: {}", storedToken.getUsuario().getNumDocumento());
        }
    }
}
