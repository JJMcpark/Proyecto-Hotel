package com.proyecto.hotel.service;

import com.proyecto.hotel.controller.request.CheckInRequestDTO;
import com.proyecto.hotel.controller.response.AlquilerResponseDTO;
import com.proyecto.hotel.model.enums.MetodoPago;

import java.util.List;

public interface AlquilerService {

    AlquilerResponseDTO registrarCheckIn(CheckInRequestDTO dto, String dniUsuario);
    void registrarCheckOut(Long idAlquiler, String dniUsuario, MetodoPago metodoPago);
    List<AlquilerResponseDTO> listarAlquileresActivos();
    AlquilerResponseDTO obtenerAlquilerPorId(Long id);

}
