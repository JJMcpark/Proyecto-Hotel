package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.TarifaDTO;
import com.proyecto.hotel.controller.request.IncrementoTarifaRequestDTO;
import com.proyecto.hotel.service.TarifaService;
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
@RequestMapping("/api/recepcion/tarifas")
@RequiredArgsConstructor
@Tag(name = "Tarifas", description = "Consulta y mantenimiento de tarifas del hotel")
public class TarifaController {

    private final TarifaService tarifaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar tarifas", description = "Devuelve todas las tarifas configuradas")
    public ResponseEntity<List<TarifaDTO>> obtenerTodasLasTarifas() {
        List<TarifaDTO> tarifas = tarifaService.obtenerTodasLasTarifas();
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener tarifa por id", description = "Consulta una tarifa específica")
    public ResponseEntity<TarifaDTO> obtenerTarifaPorId(@PathVariable Long id) {
        TarifaDTO tarifa = tarifaService.obtenerTarifaPorId(id);
        return ResponseEntity.ok(tarifa);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear tarifa", description = "Registra una nueva tarifa. Acción reservada para administrador")
    public ResponseEntity<TarifaDTO> crearTarifa(@Valid @RequestBody TarifaDTO tarifaDTO) {
        TarifaDTO tarifa = tarifaService.crearTarifa(tarifaDTO);
        return ResponseEntity.ok(tarifa);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar tarifa", description = "Actualiza una tarifa existente. Acción reservada para administrador")
    public ResponseEntity<TarifaDTO> actualizarTarifa(@PathVariable Long id, @Valid @RequestBody TarifaDTO tarifaDTO) {
        TarifaDTO tarifa = tarifaService.actualizarTarifa(id, tarifaDTO);
        return ResponseEntity.ok(tarifa);
    }

    @PatchMapping("/incremento-porcentaje")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Incrementar tarifas por porcentaje", description = "Aumenta todas las tarifas según porcentaje. Acción reservada para administrador")
    public ResponseEntity<List<TarifaDTO>> incrementarTarifas(@Valid @RequestBody IncrementoTarifaRequestDTO dto) {
        return ResponseEntity.ok(tarifaService.incrementarTarifasPorcentaje(dto.porcentaje()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar tarifa", description = "Elimina una tarifa. Acción reservada para administrador")
    public ResponseEntity<Map<String, String>> eliminarTarifa(@PathVariable Long id) {
        tarifaService.eliminarTarifa(id);
        return ResponseEntity.ok(Map.of("message", "Tarifa eliminada correctamente"));
    }
}