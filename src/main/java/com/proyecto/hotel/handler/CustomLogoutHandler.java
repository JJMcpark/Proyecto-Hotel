package com.proyecto.hotel.handler;

import com.proyecto.hotel.model.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${application.cookie.secure:false}")
    private boolean cookieSecure;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        // Always clear the refresh_token cookie
        response.addHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("refresh_token", "")
                        .httpOnly(true)
                        .secure(cookieSecure)
                        .sameSite("Lax")
                        .maxAge(0)
                        .path("/auth")
                        .build().toString());

        // Revoke the refresh token record in the database
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("refresh_token".equals(c.getName())) {
                    refreshTokenRepository.findByRefreshToken(c.getValue())
                            .ifPresent(storedToken -> {
                                storedToken.setIsLoggedOut(true);
                                refreshTokenRepository.save(storedToken);
                                log.info("Token revoked for user: {}", storedToken.getUsuario().getNumDocumento());
                            });
                    break;
                }
            }
        }
    }
}
