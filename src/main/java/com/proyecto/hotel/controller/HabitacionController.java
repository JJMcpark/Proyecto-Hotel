package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.HabitacionDTO;
import com.proyecto.hotel.service.HabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepcion/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionService habitacionService;

    @GetMapping
    public ResponseEntity<List<HabitacionDTO>> obtenerTodasLasHabitaciones(Authentication auth) {
        List<HabitacionDTO> habitaciones = habitacionService.obtenerTodasLasHabitaciones();
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<HabitacionDTO>> obtenerHabitacionesDisponibles(Authentication auth) {
        List<HabitacionDTO> habitaciones = habitacionService.obtenerHabitacionesDisponibles();
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/estado")
    public ResponseEntity<List<HabitacionDTO>> obtenerHabitacionesPorEstado(@RequestParam String estado, Authentication auth) {
        List<HabitacionDTO> habitaciones = habitacionService.obtenerHabitacionesPorEstado(estado);
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<HabitacionDTO>> obtenerHabitacionesPorTipo(@RequestParam String tipo, Authentication auth) {
        List<HabitacionDTO> habitaciones = habitacionService.obtenerHabitacionesPorTipo(tipo);
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionDTO> obtenerHabitacionPorId(@PathVariable Long id, Authentication auth) {
        HabitacionDTO habitacion = habitacionService.obtenerHabitacionPorId(id);
        return ResponseEntity.ok(habitacion);
    }

    @PostMapping
    public ResponseEntity<HabitacionDTO> crearHabitacion(@RequestBody HabitacionDTO habitacionDTO, Authentication auth) {
        HabitacionDTO habitacion = habitacionService.crearHabitacion(habitacionDTO);
        return ResponseEntity.ok(habitacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitacionDTO> actualizarHabitacion(@PathVariable Long id, @RequestBody HabitacionDTO habitacionDTO, Authentication auth) {
        HabitacionDTO habitacion = habitacionService.actualizarHabitacion(id, habitacionDTO);
        return ResponseEntity.ok(habitacion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHabitacion(@PathVariable Long id, Authentication auth) {
        habitacionService.eliminarHabitacion(id);
        return ResponseEntity.noContent().build();
    }
}