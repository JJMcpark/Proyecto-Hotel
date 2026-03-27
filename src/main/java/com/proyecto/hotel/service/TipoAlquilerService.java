package com.proyecto.hotel.service;

import com.proyecto.hotel.model.dto.TipoAlquilerDTO;
import java.util.List;

public interface TipoAlquilerService {
    List<TipoAlquilerDTO> obtenerTodosLosTiposAlquiler();
    TipoAlquilerDTO obtenerTipoAlquilerPorId(Long id);
    TipoAlquilerDTO crearTipoAlquiler(TipoAlquilerDTO tipoAlquilerDTO);
    TipoAlquilerDTO actualizarTipoAlquiler(Long id, TipoAlquilerDTO tipoAlquilerDTO);
    void eliminarTipoAlquiler(Long id);
}