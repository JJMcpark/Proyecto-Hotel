package com.proyecto.hotel.controller;

import com.proyecto.hotel.controller.request.ActualizarPerfilRequestDTO;
import com.proyecto.hotel.controller.request.CambiarPasswordRequestDTO;
import com.proyecto.hotel.model.dto.UsuarioDTO;
import com.proyecto.hotel.service.UsuarioGestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Tag(name = "Mi Perfil", description = "Consulta y actualización del perfil del usuario autenticado")
public class PerfilController {

    private final UsuarioGestionService usuarioGestionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener mi perfil", description = "Devuelve los datos del usuario autenticado")
    public ResponseEntity<UsuarioDTO> obtenerMiPerfil(Authentication authentication) {
        return ResponseEntity.ok(usuarioGestionService.obtenerMiPerfil(authentication.getName()));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Actualizar mi perfil", description = "Permite actualizar nombre y teléfono del usuario autenticado")
    public ResponseEntity<UsuarioDTO> actualizarMiPerfil(
            Authentication authentication,
            @Valid @RequestBody ActualizarPerfilRequestDTO request) {
        return ResponseEntity.ok(usuarioGestionService.actualizarMiPerfil(authentication.getName(), request));
    }

    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Cambiar mi contraseña", description = "Permite cambiar la contraseña del usuario autenticado")
    public ResponseEntity<Map<String, String>> cambiarMiPassword(
            Authentication authentication,
            @Valid @RequestBody CambiarPasswordRequestDTO request) {
        usuarioGestionService.cambiarMiPassword(authentication.getName(), request);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }
}
