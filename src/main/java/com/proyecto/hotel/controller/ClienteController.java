package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.ClienteDTO;
import com.proyecto.hotel.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recepcion/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes para operación diaria del hotel")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar clientes", description = "Devuelve todos los clientes registrados")
    public ResponseEntity<List<ClienteDTO>> obtenerTodosLosClientes(Authentication auth) {
        List<ClienteDTO> clientes = clienteService.obtenerTodosLosClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener cliente por id", description = "Consulta el detalle de un cliente específico")
    public ResponseEntity<ClienteDTO> obtenerClientePorId(@PathVariable Long id, Authentication auth) {
        ClienteDTO cliente = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Crear cliente", description = "Registra un nuevo cliente, opcionalmente asociado a una empresa")
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO, Authentication auth) {
        ClienteDTO cliente = clienteService.crearCliente(clienteDTO);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Actualizar cliente", description = "Permite corregir o actualizar datos del cliente")
    public ResponseEntity<ClienteDTO> actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO, Authentication auth) {
        ClienteDTO cliente = clienteService.actualizarCliente(id, clienteDTO);
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente. Acción reservada para administrador")
    public ResponseEntity<Map<String, String>> eliminarCliente(@PathVariable Long id, Authentication auth) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.ok(Map.of("message", "Cliente eliminado correctamente"));
    }
}