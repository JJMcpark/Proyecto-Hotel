package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.TarifaDTO;
import com.proyecto.hotel.service.TarifaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepcion/tarifas")
@RequiredArgsConstructor
public class TarifaController {

    private final TarifaService tarifaService;

    @GetMapping
    public ResponseEntity<List<TarifaDTO>> obtenerTodasLasTarifas(Authentication auth) {
        List<TarifaDTO> tarifas = tarifaService.obtenerTodasLasTarifas();
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaDTO> obtenerTarifaPorId(@PathVariable Long id, Authentication auth) {
        TarifaDTO tarifa = tarifaService.obtenerTarifaPorId(id);
        return ResponseEntity.ok(tarifa);
    }

    @PostMapping
    public ResponseEntity<TarifaDTO> crearTarifa(@RequestBody TarifaDTO tarifaDTO, Authentication auth) {
        TarifaDTO tarifa = tarifaService.crearTarifa(tarifaDTO);
        return ResponseEntity.ok(tarifa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarifaDTO> actualizarTarifa(@PathVariable Long id, @RequestBody TarifaDTO tarifaDTO, Authentication auth) {
        TarifaDTO tarifa = tarifaService.actualizarTarifa(id, tarifaDTO);
        return ResponseEntity.ok(tarifa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarifa(@PathVariable Long id, Authentication auth) {
        tarifaService.eliminarTarifa(id);
        return ResponseEntity.noContent().build();
    }
}