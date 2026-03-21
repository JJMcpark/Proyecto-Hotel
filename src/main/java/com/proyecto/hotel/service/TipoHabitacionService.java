package com.proyecto.hotel.service;

import com.proyecto.hotel.model.dto.TipoHabitacionDTO;
import java.util.List;

public interface TipoHabitacionService {
    List<TipoHabitacionDTO> obtenerTodosLosTiposHabitacion();
    TipoHabitacionDTO obtenerTipoHabitacionPorId(Long id);
    TipoHabitacionDTO crearTipoHabitacion(TipoHabitacionDTO tipoHabitacionDTO);
    TipoHabitacionDTO actualizarTipoHabitacion(Long id, TipoHabitacionDTO tipoHabitacionDTO);
    void eliminarTipoHabitacion(Long id);
}