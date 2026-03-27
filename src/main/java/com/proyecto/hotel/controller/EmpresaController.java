package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.EmpresaDTO;
import com.proyecto.hotel.service.EmpresaService;
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
@RequestMapping("/api/admin/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresas Admin", description = "Administración completa de empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar empresas", description = "Devuelve todas las empresas registradas")
    public ResponseEntity<List<EmpresaDTO>> obtenerTodasLasEmpresas() {
        return ResponseEntity.ok(empresaService.obtenerTodasLasEmpresas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener empresa por id", description = "Consulta una empresa específica")
    public ResponseEntity<EmpresaDTO> obtenerEmpresaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.obtenerEmpresaPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear empresa", description = "Registra una nueva empresa")
    public ResponseEntity<EmpresaDTO> crearEmpresa(@Valid @RequestBody EmpresaDTO empresaDTO) {
        return ResponseEntity.ok(empresaService.crearEmpresa(empresaDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar empresa", description = "Actualiza los datos de una empresa")
    public ResponseEntity<EmpresaDTO> actualizarEmpresa(@PathVariable Long id, @Valid @RequestBody EmpresaDTO empresaDTO) {
        return ResponseEntity.ok(empresaService.actualizarEmpresa(id, empresaDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar empresa", description = "Elimina una empresa si no tiene referencias activas")
    public ResponseEntity<Map<String, String>> eliminarEmpresa(@PathVariable Long id) {
        empresaService.eliminarEmpresa(id);
        return ResponseEntity.ok(Map.of("message", "Empresa eliminada correctamente"));
    }
}
