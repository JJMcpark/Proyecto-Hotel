package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.TipoHabitacionDTO;
import com.proyecto.hotel.model.entities.TipoHabitacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TipoHabitacionMapper {
    TipoHabitacionDTO toDTO(TipoHabitacion tipoHabitacion);
    TipoHabitacion toEntity(TipoHabitacionDTO tipoHabitacionDTO);
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(TipoHabitacionDTO dto, @MappingTarget TipoHabitacion entity);
}
