package com.proyecto.hotel.controller;

import com.proyecto.hotel.controller.request.ActualizarRecepcionistaRequestDTO;
import com.proyecto.hotel.controller.request.CrearRecepcionistaRequestDTO;
import com.proyecto.hotel.controller.request.ResetPasswordRequestDTO;
import com.proyecto.hotel.model.dto.UsuarioDTO;
import com.proyecto.hotel.service.UsuarioGestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/usuarios/recepcionistas")
@RequiredArgsConstructor
@Tag(name = "Usuarios Admin", description = "Gestión de recepcionistas por el administrador")
public class UsuarioAdminController {

    private final UsuarioGestionService usuarioGestionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar recepcionistas", description = "Devuelve todos los usuarios con rol recepcionista")
    public ResponseEntity<List<UsuarioDTO>> listarRecepcionistas() {
        return ResponseEntity.ok(usuarioGestionService.listarRecepcionistas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener recepcionista por id", description = "Consulta el detalle de un recepcionista específico")
    public ResponseEntity<UsuarioDTO> obtenerRecepcionistaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioGestionService.obtenerRecepcionistaPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear recepcionista", description = "Crea un usuario interno con rol recepcionista")
    public ResponseEntity<UsuarioDTO> crearRecepcionista(@Valid @RequestBody CrearRecepcionistaRequestDTO request) {
        return ResponseEntity.ok(usuarioGestionService.crearRecepcionista(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar recepcionista", description = "Actualiza datos de un recepcionista sin cambiar su rol")
    public ResponseEntity<UsuarioDTO> actualizarRecepcionista(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarRecepcionistaRequestDTO request) {
        return ResponseEntity.ok(usuarioGestionService.actualizarRecepcionista(id, request));
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Resetear contraseña de recepcionista", description = "Permite al administrador asignar una nueva contraseña a un recepcionista")
    public ResponseEntity<Map<String, String>> resetearPasswordRecepcionista(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequestDTO request) {
        usuarioGestionService.resetearPasswordRecepcionista(id, request);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }
}
