package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.enums.TipoMovimiento;
import com.proyecto.hotel.service.CajaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.proyecto.hotel.controller.request.GastoRequestDTO;
import com.proyecto.hotel.controller.response.MovimientoCajaResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/recepcion/caja")
@RequiredArgsConstructor
public class CajaController {

    private final CajaService cajaService;
    
    @PostMapping("/egreso")
    public ResponseEntity<Void> registrarSalida(
            @Valid @RequestBody GastoRequestDTO dto,
            Authentication authentication) {
        
        cajaService.registrarMovimiento(dto, TipoMovimiento.EGRESO, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/ingreso-extra")
    public ResponseEntity<Void> registrarEntradaExtra(
            @Valid @RequestBody GastoRequestDTO dto,
            Authentication authentication) {
        
        cajaService.registrarMovimiento(dto, TipoMovimiento.INGRESO, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resumen-hoy")
    public ResponseEntity<List<MovimientoCajaResponseDTO>> verCajaHoy() {
        return ResponseEntity.ok(cajaService.listarMovimientosHoy());
    }
}