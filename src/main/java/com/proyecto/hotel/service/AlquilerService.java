package com.proyecto.hotel.service;

import com.proyecto.hotel.controller.request.CheckInRequestDTO;
import com.proyecto.hotel.controller.response.AlquilerResponseDTO;
import com.proyecto.hotel.model.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AlquilerService {

    AlquilerResponseDTO registrarCheckIn(CheckInRequestDTO dto, String dniUsuario);
    AlquilerResponseDTO registrarCheckOut(Long idAlquiler, String dniUsuario, MetodoPago metodoPago);
    List<AlquilerResponseDTO> listarAlquileresActivos();
    List<AlquilerResponseDTO> listarHistorial();
    AlquilerResponseDTO obtenerAlquilerPorId(Long id);
    AlquilerResponseDTO actualizarMontos(Long id, BigDecimal subTotal, BigDecimal pagoPendiente);
    AlquilerResponseDTO actualizarFechaSalida(Long id, java.time.LocalDateTime fechaSalida);

    AlquilerResponseDTO agregarHuesped(Long idAlquiler, Long idCliente);
    AlquilerResponseDTO quitarHuesped(Long idAlquiler, Long idCliente);
    List<AlquilerResponseDTO> reporteMensual(Long idHabitacion, int mes, int anio);

    Map<String, Object> previsualizarEliminacionHistorial(LocalDate desde, LocalDate hasta);
    int eliminarHistorial(LocalDate desde, LocalDate hasta, String adminDni);

}
