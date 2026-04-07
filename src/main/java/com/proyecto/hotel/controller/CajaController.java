package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.enums.MetodoPago;
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

    @GetMapping("/movimientos-rango")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Movimientos por rango", description = "Lista movimientos de caja en un rango de fechas")
    public ResponseEntity<List<MovimientoCajaResponseDTO>> obtenerMovimientosPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(cajaService.listarMovimientosPorRango(desde, hasta));
    }

    @GetMapping("/alquiler/{alquilerId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Movimientos por alquiler", description = "Lista los movimientos de caja asociados a un alquiler específico")
    public ResponseEntity<List<MovimientoCajaResponseDTO>> obtenerMovimientosPorAlquiler(@PathVariable Long alquilerId) {
        return ResponseEntity.ok(cajaService.listarMovimientosPorAlquiler(alquilerId));
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Resumen de caja por rango de fechas", description = "Reporte con movimientos, totales y balance. Acción reservada para administrador")
    public ResponseEntity<ResumenCajaDTO> obtenerResumen(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(cajaService.obtenerResumen(desde, hasta));
    }

    @PatchMapping("/{id}/monto")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Editar monto de movimiento", description = "Permite al administrador corregir el monto de un movimiento de caja")
    public ResponseEntity<MovimientoCajaResponseDTO> actualizarMonto(
            @PathVariable Long id,
            @RequestParam java.math.BigDecimal monto) {
        return ResponseEntity.ok(cajaService.actualizarMonto(id, monto));
    }

    @PatchMapping("/{id}/cobrar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Registrar pago de empresa", description = "Cambia un movimiento PENDIENTE a INGRESO registrando el método de pago")
    public ResponseEntity<MovimientoCajaResponseDTO> cobrarMovimiento(
            @PathVariable Long id,
            @RequestParam MetodoPago metodoPago) {
        return ResponseEntity.ok(cajaService.cobrarMovimiento(id, metodoPago));
    }

    @GetMapping("/preview-eliminacion")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Preview de eliminación de movimientos", description = "Muestra cuántos movimientos se borrarían y sus totales. Sin parámetros = preview de todo")
    public ResponseEntity<java.util.Map<String, Object>> previewEliminacion(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(cajaService.previsualizarEliminacion(desde, hasta));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar movimientos de caja", description = "Borra permanentemente registros de caja. Sin parámetros = elimina todo. Con desde/hasta = solo el rango. Acción irreversible")
    public ResponseEntity<java.util.Map<String, Object>> eliminarMovimientos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Authentication authentication) {
        int deleted = cajaService.eliminarMovimientos(desde, hasta, authentication.getName());
        return ResponseEntity.ok(java.util.Map.of(
            "message", "Movimientos de caja eliminados",
            "eliminados", deleted
        ));
    }

    @PostMapping("/cobrar-lote-empresa")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cobrar lote empresa", description = "Marca como INGRESO todos los movimientos PENDIENTE de una empresa en un período")
    public ResponseEntity<java.util.List<MovimientoCajaResponseDTO>> cobrarLoteEmpresa(
            @RequestParam Long empresaId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate desde,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate hasta,
            @RequestParam MetodoPago metodoPago) {
        return ResponseEntity.ok(cajaService.cobrarLoteEmpresa(empresaId, desde, hasta, metodoPago));
    }

    @PostMapping("/cobrar-lote-ids")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cobrar lote por IDs", description = "Marca como INGRESO una lista específica de movimientos PENDIENTE identificados por sus IDs")
    public ResponseEntity<java.util.List<MovimientoCajaResponseDTO>> cobrarLoteEmpresaPorIds(
            @RequestBody java.util.List<Long> ids,
            @RequestParam MetodoPago metodoPago) {
        return ResponseEntity.ok(cajaService.cobrarLoteEmpresaPorIds(ids, metodoPago));
    }
}