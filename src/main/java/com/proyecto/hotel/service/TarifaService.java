package com.proyecto.hotel.service;

import com.proyecto.hotel.model.dto.TarifaDTO;

import java.math.BigDecimal;
import java.util.List;

public interface TarifaService {
    List<TarifaDTO> obtenerTodasLasTarifas();
    TarifaDTO obtenerTarifaPorId(Long id);
    TarifaDTO crearTarifa(TarifaDTO tarifaDTO);
    TarifaDTO actualizarTarifa(Long id, TarifaDTO tarifaDTO);
    List<TarifaDTO> incrementarTarifasPorcentaje(BigDecimal porcentaje);
    void eliminarTarifa(Long id);
}