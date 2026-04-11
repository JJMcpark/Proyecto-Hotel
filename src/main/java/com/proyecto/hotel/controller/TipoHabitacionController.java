package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.TipoHabitacionDTO;
import com.proyecto.hotel.service.TipoHabitacionService;
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
@RequestMapping("/api/recepcion/tipos-habitacion")
@RequiredArgsConstructor
@Tag(name = "Tipos de Habitación", description = "Consulta y mantenimiento de tipos de habitación")
public class TipoHabitacionController {

    private final TipoHabitacionService tipoHabitacionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar tipos de habitación", description = "Devuelve todos los tipos de habitación disponibles")
    public ResponseEntity<List<TipoHabitacionDTO>> obtenerTodosLosTiposHabitacion() {
        List<TipoHabitacionDTO> tipos = tipoHabitacionService.obtenerTodosLosTiposHabitacion();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener tipo de habitación por id", description = "Consulta un tipo de habitación específico")
    public ResponseEntity<TipoHabitacionDTO> obtenerTipoHabitacionPorId(@PathVariable Long id) {
        TipoHabitacionDTO tipo = tipoHabitacionService.obtenerTipoHabitacionPorId(id);
        return ResponseEntity.ok(tipo);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear tipo de habitación", description = "Registra un nuevo tipo de habitación. Acción reservada para administrador")
    public ResponseEntity<TipoHabitacionDTO> crearTipoHabitacion(@Valid @RequestBody TipoHabitacionDTO tipoHabitacionDTO) {
        TipoHabitacionDTO tipo = tipoHabitacionService.crearTipoHabitacion(tipoHabitacionDTO);
        return ResponseEntity.ok(tipo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar tipo de habitación", description = "Actualiza un tipo de habitación existente. Acción reservada para administrador")
    public ResponseEntity<TipoHabitacionDTO> actualizarTipoHabitacion(@PathVariable Long id, @Valid @RequestBody TipoHabitacionDTO tipoHabitacionDTO) {
        TipoHabitacionDTO tipo = tipoHabitacionService.actualizarTipoHabitacion(id, tipoHabitacionDTO);
        return ResponseEntity.ok(tipo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar tipo de habitación", description = "Elimina un tipo de habitación. Acción reservada para administrador")
    public ResponseEntity<Map<String, String>> eliminarTipoHabitacion(@PathVariable Long id) {
        tipoHabitacionService.eliminarTipoHabitacion(id);
        return ResponseEntity.ok(Map.of("message", "Tipo de habitación eliminado correctamente"));
    }
}