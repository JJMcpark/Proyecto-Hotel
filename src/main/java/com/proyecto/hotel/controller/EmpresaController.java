package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.EmpresaDTO;
import com.proyecto.hotel.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<List<EmpresaDTO>> obtenerTodasLasEmpresas() {
        return ResponseEntity.ok(empresaService.obtenerTodasLasEmpresas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDTO> obtenerEmpresaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.obtenerEmpresaPorId(id));
    }

    @PostMapping
    public ResponseEntity<EmpresaDTO> crearEmpresa(@RequestBody EmpresaDTO empresaDTO) {
        return ResponseEntity.ok(empresaService.crearEmpresa(empresaDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDTO> actualizarEmpresa(@PathVariable Long id, @RequestBody EmpresaDTO empresaDTO) {
        return ResponseEntity.ok(empresaService.actualizarEmpresa(id, empresaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Long id) {
        empresaService.eliminarEmpresa(id);
        return ResponseEntity.noContent().build();
    }
}
