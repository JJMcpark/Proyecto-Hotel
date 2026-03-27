package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.EmpresaDTO;
import com.proyecto.hotel.service.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recepcion/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresas Consulta", description = "Consulta de empresas para asociación con clientes en recepción")
public class EmpresaRecepcionController {

    private final EmpresaService empresaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar empresas para recepción", description = "Devuelve empresas en modo solo lectura para asociarlas a clientes")
    public ResponseEntity<List<EmpresaDTO>> obtenerTodasLasEmpresas() {
        return ResponseEntity.ok(empresaService.obtenerTodasLasEmpresas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener empresa por id para recepción", description = "Consulta una empresa en modo solo lectura")
    public ResponseEntity<EmpresaDTO> obtenerEmpresaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.obtenerEmpresaPorId(id));
    }
}
