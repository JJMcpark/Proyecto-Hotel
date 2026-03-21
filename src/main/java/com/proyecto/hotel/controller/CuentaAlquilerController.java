package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.CuentaAlquilerDTO;
import com.proyecto.hotel.service.CuentaAlquilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepcion/alquiler/{alquilerId}/cuenta")
@RequiredArgsConstructor
public class CuentaAlquilerController {

    private final CuentaAlquilerService cuentaAlquilerService;

    @GetMapping
    public ResponseEntity<List<CuentaAlquilerDTO>> obtenerCargos(@PathVariable Long alquilerId) {
        return ResponseEntity.ok(cuentaAlquilerService.obtenerCuentasPorAlquiler(alquilerId));
    }

    @PostMapping
    public ResponseEntity<CuentaAlquilerDTO> agregarCargo(
            @PathVariable Long alquilerId,
            @RequestBody CuentaAlquilerDTO dto) {
        return ResponseEntity.ok(cuentaAlquilerService.agregarCargo(alquilerId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaAlquilerDTO> actualizarCargo(
            @PathVariable Long alquilerId,
            @PathVariable Long id,
            @RequestBody CuentaAlquilerDTO dto) {
        return ResponseEntity.ok(cuentaAlquilerService.actualizarCargo(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCargo(
            @PathVariable Long alquilerId,
            @PathVariable Long id) {
        cuentaAlquilerService.eliminarCargo(id);
        return ResponseEntity.noContent().build();
    }
}
