package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.HabitacionDTO;
import com.proyecto.hotel.service.HabitacionService;
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
@RequestMapping("/api/recepcion/habitaciones")
@RequiredArgsConstructor
@Tag(name = "Habitaciones", description = "Consulta y administración de habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar habitaciones", description = "Devuelve todas las habitaciones registradas")
    public ResponseEntity<List<HabitacionDTO>> obtenerTodasLasHabitaciones(Authentication auth) {
        List<HabitacionDTO> habitaciones = habitacionService.obtenerTodasLasHabitaciones();
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar habitaciones disponibles", description = "Devuelve solo habitaciones en estado DISPONIBLE")
    public ResponseEntity<List<HabitacionDTO>> obtenerHabitacionesDisponibles(Authentication auth) {
        List<HabitacionDTO> habitaciones = habitacionService.obtenerHabitacionesDisponibles();
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Filtrar por estado", description = "Busca habitaciones según su estado actual")
    public ResponseEntity<List<HabitacionDTO>> obtenerHabitacionesPorEstado(@RequestParam String estado, Authentication auth) {
        List<HabitacionDTO> habitaciones = habitacionService.obtenerHabitacionesPorEstado(estado);
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/tipo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Filtrar por tipo", description = "Busca habitaciones según el tipo de habitación")
    public ResponseEntity<List<HabitacionDTO>> obtenerHabitacionesPorTipo(@RequestParam String tipo, Authentication auth) {
        List<HabitacionDTO> habitaciones = habitacionService.obtenerHabitacionesPorTipo(tipo);
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener habitación por id", description = "Consulta el detalle de una habitación específica")
    public ResponseEntity<HabitacionDTO> obtenerHabitacionPorId(@PathVariable Long id, Authentication auth) {
        HabitacionDTO habitacion = habitacionService.obtenerHabitacionPorId(id);
        return ResponseEntity.ok(habitacion);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear habitación", description = "Registra una nueva habitación. Acción reservada para administrador")
    public ResponseEntity<HabitacionDTO> crearHabitacion(@Valid @RequestBody HabitacionDTO habitacionDTO, Authentication auth) {
        HabitacionDTO habitacion = habitacionService.crearHabitacion(habitacionDTO);
        return ResponseEntity.ok(habitacion);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar habitación", description = "Actualiza los datos de una habitación. Acción reservada para administrador")
    public ResponseEntity<HabitacionDTO> actualizarHabitacion(@PathVariable Long id, @Valid @RequestBody HabitacionDTO habitacionDTO, Authentication auth) {
        HabitacionDTO habitacion = habitacionService.actualizarHabitacion(id, habitacionDTO);
        return ResponseEntity.ok(habitacion);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Cambiar estado de habitación", description = "Cambia el estado de una habitación (DISPONIBLE, OCUPADA, LIMPIEZA, MANTENIMIENTO)")
    public ResponseEntity<HabitacionDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado, Authentication auth) {
        HabitacionDTO habitacion = habitacionService.cambiarEstado(id, estado);
        return ResponseEntity.ok(habitacion);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar habitación", description = "Elimina una habitación. Acción reservada para administrador")
    public ResponseEntity<Map<String, String>> eliminarHabitacion(@PathVariable Long id, Authentication auth) {
        habitacionService.eliminarHabitacion(id);
        return ResponseEntity.ok(Map.of("message", "Habitación eliminada correctamente"));
    }
}