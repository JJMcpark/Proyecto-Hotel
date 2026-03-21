package com.proyecto.hotel.service;

import com.proyecto.hotel.controller.request.GastoRequestDTO;
import com.proyecto.hotel.controller.response.MovimientoCajaResponseDTO;
import com.proyecto.hotel.model.enums.TipoMovimiento;

import java.util.List;

public interface CajaService {
    void registrarMovimiento(GastoRequestDTO dto, TipoMovimiento tipo, String dniUsuario);
    List<MovimientoCajaResponseDTO> listarMovimientosHoy();
}
