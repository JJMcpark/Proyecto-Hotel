package com.proyecto.hotel.controller;

import com.proyecto.hotel.service.AlquilerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.proyecto.hotel.controller.request.ActualizarMontosAlquilerRequestDTO;
import com.proyecto.hotel.controller.request.CheckInRequestDTO;
import com.proyecto.hotel.controller.response.AlquilerResponseDTO;
import com.proyecto.hotel.model.enums.MetodoPago;

import java.util.List;

@RestController
@RequestMapping("/api/recepcion/alquiler")
@RequiredArgsConstructor
@Tag(name = "Alquileres", description = "Operaciones de check-in, check-out y consulta de alquileres")
public class AlquilerController {

    private final AlquilerService alquilerService;

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Registrar check-in", description = "Crea un alquiler activo para un cliente en una habitación disponible")
    public ResponseEntity<AlquilerResponseDTO> checkIn(
            @Valid @RequestBody CheckInRequestDTO dto, 
            Authentication auth) {
        return ResponseEntity.ok(alquilerService.registrarCheckIn(dto, auth.getName()));
    }

    @PostMapping("/{id}/check-out")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Registrar check-out", description = "Finaliza un alquiler y liquida el pago pendiente si existe")
    public ResponseEntity<AlquilerResponseDTO> checkOut(
            @PathVariable Long id,
            @RequestParam(required = false) MetodoPago metodoPago,
            Authentication auth) {
        return ResponseEntity.ok(alquilerService.registrarCheckOut(id, auth.getName(), metodoPago));
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar alquileres activos", description = "Devuelve los alquileres que se encuentran actualmente en estado ACTIVO")
    public ResponseEntity<List<AlquilerResponseDTO>> listarActivos() {
        return ResponseEntity.ok(alquilerService.listarAlquileresActivos());
    }

    @GetMapping("/historial")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Historial de alquileres", description = "Devuelve los alquileres finalizados. Acción reservada para administrador")
    public ResponseEntity<List<AlquilerResponseDTO>> listarHistorial() {
        return ResponseEntity.ok(alquilerService.listarHistorial());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener alquiler por id", description = "Consulta el detalle de un alquiler específico")
    public ResponseEntity<AlquilerResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.obtenerAlquilerPorId(id));
    }

    @PatchMapping("/{id}/montos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar montos de alquiler", description = "Permite ajustar subtotal y pendiente. Acción reservada para administrador")
    public ResponseEntity<AlquilerResponseDTO> actualizarMontos(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarMontosAlquilerRequestDTO dto) {
        return ResponseEntity.ok(alquilerService.actualizarMontos(id, dto.subTotal(), dto.pagoPendiente()));
    }

    @PostMapping("/{id}/huespedes/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Agregar huésped a alquiler", description = "Agrega un cliente como huésped adicional a un alquiler activo")
    public ResponseEntity<AlquilerResponseDTO> agregarHuesped(
            @PathVariable Long id,
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(alquilerService.agregarHuesped(id, clienteId));
    }

    @DeleteMapping("/{id}/huespedes/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Quitar huésped de alquiler", description = "Elimina un huésped adicional de un alquiler activo")
    public ResponseEntity<AlquilerResponseDTO> quitarHuesped(
            @PathVariable Long id,
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(alquilerService.quitarHuesped(id, clienteId));
    }

    @GetMapping("/reporte-mensual")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Reporte mensual de habitación", description = "Lista los alquileres de una habitación en un mes y año específico")
    public ResponseEntity<List<AlquilerResponseDTO>> reporteMensual(
            @RequestParam Long habitacionId,
            @RequestParam int mes,
            @RequestParam int anio) {
        return ResponseEntity.ok(alquilerService.reporteMensual(habitacionId, mes, anio));
    }
}