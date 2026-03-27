package com.proyecto.hotel.controller;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.CuentaAlquilerDTO;
import com.proyecto.hotel.model.entities.CuentaAlquiler;
import com.proyecto.hotel.model.enums.EstadoCuenta;
import com.proyecto.hotel.model.enums.MetodoPago;
import com.proyecto.hotel.model.repository.AlquilerRepository;
import com.proyecto.hotel.model.repository.CuentaAlquilerRepository;
import com.proyecto.hotel.service.CuentaAlquilerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recepcion/alquiler/{alquilerId}/cuenta")
@RequiredArgsConstructor
@Tag(name = "Cuenta de Alquiler", description = "Gestión de cargos adicionales asociados a un alquiler")
public class CuentaAlquilerController {

    private final CuentaAlquilerService cuentaAlquilerService;
    private final AlquilerRepository alquilerRepository;
    private final CuentaAlquilerRepository cuentaAlquilerRepository;

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
            @Valid @RequestBody CuentaAlquilerDTO dto,
            Authentication authentication) {
        boolean isRecepcionista = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_RECEPCIONISTA".equals(a.getAuthority()));
        boolean alquilerEsEmpresa = alquilerRepository.countEmpresaAlquilerById(alquilerId) > 0;
        if (isRecepcionista && alquilerEsEmpresa) {
            dto.setPrecioUnit(BigDecimal.ZERO);
        }
        return ResponseEntity.ok(cuentaAlquilerService.agregarCargo(alquilerId, dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Actualizar cargo", description = "Modifica un cargo existente. Recepcionista solo puede marcar pagado en clientes no empresa")
    public ResponseEntity<CuentaAlquilerDTO> actualizarCargo(
            @PathVariable Long alquilerId,
            @PathVariable Long id,
            @Valid @RequestBody CuentaAlquilerDTO dto,
            @RequestParam(required = false) MetodoPago metodoPago,
            Authentication authentication) {
        CuentaAlquiler cuentaExistente = cuentaAlquilerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Cargo no encontrado con id: " + id));

        if (!alquilerId.equals(cuentaExistente.getAlquiler().getId())) {
            throw new BadRequestException("El cargo no pertenece al alquiler indicado");
        }

        boolean isRecepcionista = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_RECEPCIONISTA".equals(a.getAuthority()));

        if (isRecepcionista) {
            boolean alquilerEsEmpresa = alquilerRepository.countEmpresaAlquilerById(alquilerId) > 0;
            if (alquilerEsEmpresa) {
                throw new BadRequestException("No autorizado para marcar como pagado en clientes empresa");
            }

            dto.setDescripcion(cuentaExistente.getDescripcion());
            dto.setPrecioUnit(cuentaExistente.getPrecioUnit());
            dto.setCantidad(cuentaExistente.getCantidad());
            dto.setEstado(EstadoCuenta.PAGADO.name());
        }

        return ResponseEntity.ok(cuentaAlquilerService.actualizarCargo(id, dto, authentication.getName(), metodoPago));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Eliminar cargo", description = "Elimina un cargo de la cuenta. Si ya estaba pagado, registra EGRESO compensatorio en caja.")
    public ResponseEntity<Map<String, String>> eliminarCargo(
            @PathVariable Long alquilerId,
            @PathVariable Long id,
            Authentication authentication) {
        cuentaAlquilerService.eliminarCargo(id, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Cargo eliminado correctamente"));
    }
}
