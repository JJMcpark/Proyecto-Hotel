package com.proyecto.hotel.service;

import com.proyecto.hotel.model.dto.CuentaAlquilerDTO;
import com.proyecto.hotel.model.enums.MetodoPago;

import java.util.List;

public interface CuentaAlquilerService {
    List<CuentaAlquilerDTO> obtenerCuentasPorAlquiler(Long alquilerId);
    CuentaAlquilerDTO agregarCargo(Long alquilerId, CuentaAlquilerDTO dto);
    CuentaAlquilerDTO actualizarCargo(Long id, CuentaAlquilerDTO dto, String dniUsuario, MetodoPago metodoPago);
    void eliminarCargo(Long id);
}
