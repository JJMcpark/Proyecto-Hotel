package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.HabitacionDTO;
import com.proyecto.hotel.model.entities.Habitacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TipoHabitacionMapper.class})
public interface HabitacionMapper {
    HabitacionDTO toDTO(Habitacion habitacion);
    Habitacion toEntity(HabitacionDTO habitacionDTO);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoHabitacion", ignore = true)
    void updateEntityFromDTO(HabitacionDTO dto, @MappingTarget Habitacion entity);
}
