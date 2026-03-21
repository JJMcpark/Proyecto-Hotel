package com.proyecto.hotel.controller;

import com.proyecto.hotel.model.dto.ClienteDTO;
import com.proyecto.hotel.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepcion/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerTodosLosClientes(Authentication auth) {
        List<ClienteDTO> clientes = clienteService.obtenerTodosLosClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerClientePorId(@PathVariable Long id, Authentication auth) {
        ClienteDTO cliente = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@RequestBody ClienteDTO clienteDTO, Authentication auth) {
        ClienteDTO cliente = clienteService.crearCliente(clienteDTO);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO, Authentication auth) {
        ClienteDTO cliente = clienteService.actualizarCliente(id, clienteDTO);
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id, Authentication auth) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}