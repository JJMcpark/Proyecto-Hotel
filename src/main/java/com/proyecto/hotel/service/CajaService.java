package com.proyecto.hotel.service;

import com.proyecto.hotel.controller.request.GastoRequestDTO;
import com.proyecto.hotel.controller.response.MovimientoCajaResponseDTO;
import com.proyecto.hotel.controller.response.ResumenCajaDTO;
import com.proyecto.hotel.model.enums.MetodoPago;
import com.proyecto.hotel.model.enums.TipoMovimiento;

import java.time.LocalDate;
import java.util.List;

public interface CajaService {
    MovimientoCajaResponseDTO registrarMovimiento(GastoRequestDTO dto, TipoMovimiento tipo, String dniUsuario);
    List<MovimientoCajaResponseDTO> listarMovimientosHoy();
    List<MovimientoCajaResponseDTO> listarMovimientosPorRango(LocalDate desde, LocalDate hasta);
    List<MovimientoCajaResponseDTO> listarMovimientosPorAlquiler(Long alquilerId);
    ResumenCajaDTO obtenerResumen(LocalDate desde, LocalDate hasta);
    MovimientoCajaResponseDTO actualizarMonto(Long id, java.math.BigDecimal monto);
    MovimientoCajaResponseDTO cobrarMovimiento(Long id, MetodoPago metodoPago);
    List<MovimientoCajaResponseDTO> cobrarLoteEmpresa(Long empresaId, LocalDate desde, LocalDate hasta, MetodoPago metodoPago);
}
