package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.TipoHabitacionDTO;
import com.proyecto.hotel.model.entities.TipoHabitacion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TipoHabitacionMapper {
    TipoHabitacionDTO toDTO(TipoHabitacion tipoHabitacion);
    TipoHabitacion toEntity(TipoHabitacionDTO tipoHabitacionDTO);
}
