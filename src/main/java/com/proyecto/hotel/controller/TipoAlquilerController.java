package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.TipoAlquilerDTO;
import com.proyecto.hotel.service.TipoAlquilerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recepcion/tipos-alquiler")
@RequiredArgsConstructor
@Tag(name = "Tipos de Alquiler", description = "Consulta y mantenimiento de tipos de alquiler")
public class TipoAlquilerController {

    private final TipoAlquilerService tipoAlquilerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar tipos de alquiler", description = "Devuelve los tipos de alquiler configurados")
    public ResponseEntity<List<TipoAlquilerDTO>> obtenerTodosLosTiposAlquiler(Authentication auth) {
        List<TipoAlquilerDTO> tipos = tipoAlquilerService.obtenerTodosLosTiposAlquiler();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener tipo de alquiler por id", description = "Consulta un tipo de alquiler específico")
    public ResponseEntity<TipoAlquilerDTO> obtenerTipoAlquilerPorId(@PathVariable Long id, Authentication auth) {
        TipoAlquilerDTO tipo = tipoAlquilerService.obtenerTipoAlquilerPorId(id);
        return ResponseEntity.ok(tipo);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear tipo de alquiler", description = "Registra un nuevo tipo de alquiler. Acción reservada para administrador")
    public ResponseEntity<TipoAlquilerDTO> crearTipoAlquiler(@Valid @RequestBody TipoAlquilerDTO tipoAlquilerDTO, Authentication auth) {
        TipoAlquilerDTO tipo = tipoAlquilerService.crearTipoAlquiler(tipoAlquilerDTO);
        return ResponseEntity.ok(tipo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar tipo de alquiler", description = "Actualiza un tipo de alquiler existente. Acción reservada para administrador")
    public ResponseEntity<TipoAlquilerDTO> actualizarTipoAlquiler(@PathVariable Long id, @Valid @RequestBody TipoAlquilerDTO tipoAlquilerDTO, Authentication auth) {
        TipoAlquilerDTO tipo = tipoAlquilerService.actualizarTipoAlquiler(id, tipoAlquilerDTO);
        return ResponseEntity.ok(tipo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar tipo de alquiler", description = "Elimina un tipo de alquiler. Acción reservada para administrador")
    public ResponseEntity<Map<String, String>> eliminarTipoAlquiler(@PathVariable Long id, Authentication auth) {
        tipoAlquilerService.eliminarTipoAlquiler(id);
        return ResponseEntity.ok(Map.of("message", "Tipo de alquiler eliminado correctamente"));
    }
}