package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.enums.TipoMovimiento;
import com.proyecto.hotel.service.CajaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.proyecto.hotel.controller.request.GastoRequestDTO;
import com.proyecto.hotel.controller.response.MovimientoCajaResponseDTO;
import com.proyecto.hotel.controller.response.ResumenCajaDTO;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/recepcion/caja")
@RequiredArgsConstructor
@Tag(name = "Caja", description = "Movimientos de ingresos, egresos y consulta de caja diaria")
public class CajaController {

    private final CajaService cajaService;
    
    @PostMapping("/egreso")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Registrar egreso", description = "Registra una salida de dinero en caja")
    public ResponseEntity<MovimientoCajaResponseDTO> registrarSalida(
            @Valid @RequestBody GastoRequestDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(cajaService.registrarMovimiento(dto, TipoMovimiento.EGRESO, authentication.getName()));
    }

    @PostMapping("/ingreso-extra")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Registrar ingreso extra", description = "Registra un ingreso manual adicional en caja")
    public ResponseEntity<MovimientoCajaResponseDTO> registrarEntradaExtra(
            @Valid @RequestBody GastoRequestDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(cajaService.registrarMovimiento(dto, TipoMovimiento.INGRESO, authentication.getName()));
    }

    @GetMapping("/resumen-hoy")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Ver caja del día", description = "Lista todos los movimientos registrados en la fecha actual")
    public ResponseEntity<List<MovimientoCajaResponseDTO>> verCajaHoy() {
        return ResponseEntity.ok(cajaService.listarMovimientosHoy());
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Resumen de caja por rango de fechas", description = "Reporte con movimientos, totales y balance. Acción reservada para administrador")
    public ResponseEntity<ResumenCajaDTO> obtenerResumen(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(cajaService.obtenerResumen(desde, hasta));
    }
}