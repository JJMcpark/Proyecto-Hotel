package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.CuentaAlquilerDTO;
import com.proyecto.hotel.service.CuentaAlquilerService;
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
@RequestMapping("/api/recepcion/alquiler/{alquilerId}/cuenta")
@RequiredArgsConstructor
@Tag(name = "Cuenta de Alquiler", description = "Gestión de cargos adicionales asociados a un alquiler")
public class CuentaAlquilerController {

    private final CuentaAlquilerService cuentaAlquilerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar cargos de un alquiler", description = "Devuelve los cargos adicionales registrados para un alquiler")
    public ResponseEntity<List<CuentaAlquilerDTO>> obtenerCargos(@PathVariable Long alquilerId) {
        return ResponseEntity.ok(cuentaAlquilerService.obtenerCuentasPorAlquiler(alquilerId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Agregar cargo", description = "Registra un nuevo cargo o consumo en la cuenta del alquiler")
    public ResponseEntity<CuentaAlquilerDTO> agregarCargo(
            @PathVariable Long alquilerId,
            @Valid @RequestBody CuentaAlquilerDTO dto) {
        return ResponseEntity.ok(cuentaAlquilerService.agregarCargo(alquilerId, dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar cargo", description = "Modifica un cargo existente. Acción reservada para administrador")
    public ResponseEntity<CuentaAlquilerDTO> actualizarCargo(
            @PathVariable Long alquilerId,
            @PathVariable Long id,
            @Valid @RequestBody CuentaAlquilerDTO dto) {
        return ResponseEntity.ok(cuentaAlquilerService.actualizarCargo(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Eliminar cargo", description = "Elimina un cargo de la cuenta")
    public ResponseEntity<Map<String, String>> eliminarCargo(
            @PathVariable Long alquilerId,
            @PathVariable Long id) {
        cuentaAlquilerService.eliminarCargo(id);
        return ResponseEntity.ok(Map.of("message", "Cargo eliminado correctamente"));
    }
}
