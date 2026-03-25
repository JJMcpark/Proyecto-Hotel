package com.proyecto.hotel.service;

import com.proyecto.hotel.controller.request.GastoRequestDTO;
import com.proyecto.hotel.controller.response.MovimientoCajaResponseDTO;
import com.proyecto.hotel.controller.response.ResumenCajaDTO;
import com.proyecto.hotel.model.enums.TipoMovimiento;

import java.time.LocalDate;
import java.util.List;

public interface CajaService {
    MovimientoCajaResponseDTO registrarMovimiento(GastoRequestDTO dto, TipoMovimiento tipo, String dniUsuario);
    List<MovimientoCajaResponseDTO> listarMovimientosHoy();
    List<MovimientoCajaResponseDTO> listarMovimientosPorRango(LocalDate desde, LocalDate hasta);
    ResumenCajaDTO obtenerResumen(LocalDate desde, LocalDate hasta);
}
