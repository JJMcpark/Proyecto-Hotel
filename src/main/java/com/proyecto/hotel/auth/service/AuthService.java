package com.proyecto.hotel.auth.service;

import com.proyecto.hotel.auth.exception.InvalidRoleException;
import com.proyecto.hotel.auth.exception.TokenValidationException;
import com.proyecto.hotel.auth.jwt.JwtService;
import com.proyecto.hotel.auth.request.LoginRequest;
import com.proyecto.hotel.auth.response.AuthResponse;
import com.proyecto.hotel.model.entities.RefreshToken;
import com.proyecto.hotel.model.entities.Usuario;
import com.proyecto.hotel.model.repository.RefreshTokenRepository;
import com.proyecto.hotel.model.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        return loginWithRole(request, null);
    }

    @Transactional
    public AuthResponse loginWithRole(LoginRequest request, String requiredRole) {
        String numDocumento = request.getNumDocumento();

        loginAttemptService.validateLoginAllowed(numDocumento);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(numDocumento, request.getPassword())
            );
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(numDocumento);
            throw e;
        }

        // Obtener usuario autenticado
        Usuario usuario = usuarioRepository.findByNumDocumento(numDocumento)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + numDocumento));

        if (requiredRole != null && !usuario.getRol().getNombre().equals(requiredRole)) {
            throw new InvalidRoleException("Acceso denegado para el rol solicitado");
        }

        loginAttemptService.loginSucceeded(numDocumento);

        // Generar tokens
        String accessToken = jwtService.generateAccessToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        // Guardar refresh token en BD
        saveRefreshToken(usuario, refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .numDocumento(usuario.getNumDocumento())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().getNombre())
                .build();
    }

    @Transactional
    public void logout() {
        // Obtener usuario actual del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getName() != null) {
            Usuario usuario = usuarioRepository.findByNumDocumento(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Revocar todos los refresh tokens del usuario
            revokeAllUserTokens(usuario);
        }
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        // Extraer usuario del refresh token
        String numDocumento = jwtService.extractUsername(refreshToken);
        Usuario usuario = usuarioRepository.findByNumDocumento(numDocumento)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + numDocumento));

        // Validar refresh token
        boolean isValidRefreshToken = jwtService.isRefreshTokenValid(refreshToken, usuario);
        if (!isValidRefreshToken) {
            throw new TokenValidationException("Refresh token inválido o expirado");
        }

        // Generar nuevo access token
        String newAccessToken = jwtService.generateAccessToken(usuario);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .numDocumento(usuario.getNumDocumento())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().getNombre())
                .build();
    }

    private void saveRefreshToken(Usuario usuario, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .usuario(usuario)
                .isLoggedOut(false)
                .build();
        refreshTokenRepository.save(token);
    }

    private void revokeAllUserTokens(Usuario usuario) {
        var allUserTokens = refreshTokenRepository.findAllRefreshTokenByUsuario(usuario.getId());
        if (!allUserTokens.isEmpty()) {
            allUserTokens.forEach(token -> token.setIsLoggedOut(true));
            refreshTokenRepository.saveAll(allUserTokens);
        }
    }
}
