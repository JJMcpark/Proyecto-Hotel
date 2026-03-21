package com.proyecto.hotel.service;

import com.proyecto.hotel.model.dto.ClienteDTO;
import java.util.List;

public interface ClienteService {
    List<ClienteDTO> obtenerTodosLosClientes();
    ClienteDTO obtenerClientePorId(Long id);
    ClienteDTO crearCliente(ClienteDTO clienteDTO);
    ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO);
    void eliminarCliente(Long id);
}