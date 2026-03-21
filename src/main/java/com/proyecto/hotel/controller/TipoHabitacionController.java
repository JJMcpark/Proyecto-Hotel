package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.TipoHabitacionDTO;
import com.proyecto.hotel.service.TipoHabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepcion/tipos-habitacion")
@RequiredArgsConstructor
public class TipoHabitacionController {

    private final TipoHabitacionService tipoHabitacionService;

    @GetMapping
    public ResponseEntity<List<TipoHabitacionDTO>> obtenerTodosLosTiposHabitacion(Authentication auth) {
        List<TipoHabitacionDTO> tipos = tipoHabitacionService.obtenerTodosLosTiposHabitacion();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoHabitacionDTO> obtenerTipoHabitacionPorId(@PathVariable Long id, Authentication auth) {
        TipoHabitacionDTO tipo = tipoHabitacionService.obtenerTipoHabitacionPorId(id);
        return ResponseEntity.ok(tipo);
    }

    @PostMapping
    public ResponseEntity<TipoHabitacionDTO> crearTipoHabitacion(@RequestBody TipoHabitacionDTO tipoHabitacionDTO, Authentication auth) {
        TipoHabitacionDTO tipo = tipoHabitacionService.crearTipoHabitacion(tipoHabitacionDTO);
        return ResponseEntity.ok(tipo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoHabitacionDTO> actualizarTipoHabitacion(@PathVariable Long id, @RequestBody TipoHabitacionDTO tipoHabitacionDTO, Authentication auth) {
        TipoHabitacionDTO tipo = tipoHabitacionService.actualizarTipoHabitacion(id, tipoHabitacionDTO);
        return ResponseEntity.ok(tipo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipoHabitacion(@PathVariable Long id, Authentication auth) {
        tipoHabitacionService.eliminarTipoHabitacion(id);
        return ResponseEntity.noContent().build();
    }
}