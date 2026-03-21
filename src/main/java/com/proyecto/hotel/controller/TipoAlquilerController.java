package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.TipoAlquilerDTO;
import com.proyecto.hotel.service.TipoAlquilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepcion/tipos-alquiler")
@RequiredArgsConstructor
public class TipoAlquilerController {

    private final TipoAlquilerService tipoAlquilerService;

    @GetMapping
    public ResponseEntity<List<TipoAlquilerDTO>> obtenerTodosLosTiposAlquiler(Authentication auth) {
        List<TipoAlquilerDTO> tipos = tipoAlquilerService.obtenerTodosLosTiposAlquiler();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoAlquilerDTO> obtenerTipoAlquilerPorId(@PathVariable Long id, Authentication auth) {
        TipoAlquilerDTO tipo = tipoAlquilerService.obtenerTipoAlquilerPorId(id);
        return ResponseEntity.ok(tipo);
    }

    @PostMapping
    public ResponseEntity<TipoAlquilerDTO> crearTipoAlquiler(@RequestBody TipoAlquilerDTO tipoAlquilerDTO, Authentication auth) {
        TipoAlquilerDTO tipo = tipoAlquilerService.crearTipoAlquiler(tipoAlquilerDTO);
        return ResponseEntity.ok(tipo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoAlquilerDTO> actualizarTipoAlquiler(@PathVariable Long id, @RequestBody TipoAlquilerDTO tipoAlquilerDTO, Authentication auth) {
        TipoAlquilerDTO tipo = tipoAlquilerService.actualizarTipoAlquiler(id, tipoAlquilerDTO);
        return ResponseEntity.ok(tipo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipoAlquiler(@PathVariable Long id, Authentication auth) {
        tipoAlquilerService.eliminarTipoAlquiler(id);
        return ResponseEntity.noContent().build();
    }
}