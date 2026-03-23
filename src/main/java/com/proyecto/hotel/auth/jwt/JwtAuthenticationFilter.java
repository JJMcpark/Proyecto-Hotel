package com.proyecto.hotel.auth.jwt;

import com.proyecto.hotel.model.entities.Usuario;
import com.proyecto.hotel.model.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final Long tokenUserId = jwtService.extractUserId(jwt);
            final String numDocumento = jwtService.extractUsername(jwt);
            final String tokenRole = jwtService.extractRole(jwt);

            if (StringUtils.hasText(numDocumento) && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<Usuario> usuarioOpt = tokenUserId != null
                        ? usuarioRepository.findUsuarioById(tokenUserId)
                        : usuarioRepository.findByNumDocumento(numDocumento);

                if (usuarioOpt.isEmpty()) {
                    filterChain.doFilter(request, response);
                    return;
                }

                Usuario usuario = usuarioOpt.get();
                if (jwtService.isTokenExpired(jwt)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                boolean idMatches = tokenUserId == null || tokenUserId.equals(usuario.getId());
                String dbRole = Optional.ofNullable(usuario.getAuthorities())
                        .flatMap(authorities -> authorities.stream().findFirst())
                        .map(GrantedAuthority::getAuthority)
                        .orElse(null);
                boolean roleMatches = tokenRole == null || tokenRole.equals(dbRole);

                if (!idMatches || !roleMatches) {
                    filterChain.doFilter(request, response);
                    return;
                }

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        usuario, null, usuario.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }
}
