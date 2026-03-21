package com.proyecto.hotel.service;

import com.proyecto.hotel.model.dto.CuentaAlquilerDTO;

import java.util.List;

public interface CuentaAlquilerService {
    List<CuentaAlquilerDTO> obtenerCuentasPorAlquiler(Long alquilerId);
    CuentaAlquilerDTO agregarCargo(Long alquilerId, CuentaAlquilerDTO dto);
    CuentaAlquilerDTO actualizarCargo(Long id, CuentaAlquilerDTO dto);
    void eliminarCargo(Long id);
}
