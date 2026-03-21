package com.proyecto.hotel.service;

import com.proyecto.hotel.model.dto.HabitacionDTO;
import java.util.List;

public interface HabitacionService {
    List<HabitacionDTO> obtenerTodasLasHabitaciones();
    List<HabitacionDTO> obtenerHabitacionesDisponibles();
    List<HabitacionDTO> obtenerHabitacionesPorEstado(String estado);
    List<HabitacionDTO> obtenerHabitacionesPorTipo(String tipo);
    HabitacionDTO obtenerHabitacionPorId(Long id);
    HabitacionDTO crearHabitacion(HabitacionDTO habitacionDTO);
    HabitacionDTO actualizarHabitacion(Long id, HabitacionDTO habitacionDTO);
    void eliminarHabitacion(Long id);
}