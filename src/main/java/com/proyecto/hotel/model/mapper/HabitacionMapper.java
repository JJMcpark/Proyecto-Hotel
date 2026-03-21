package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.HabitacionDTO;
import com.proyecto.hotel.model.entities.Habitacion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TipoHabitacionMapper.class})
public interface HabitacionMapper {
    HabitacionDTO toDTO(Habitacion habitacion);
    Habitacion toEntity(HabitacionDTO habitacionDTO);
    void updateEntityFromDTO(HabitacionDTO dto, @MappingTarget Habitacion entity);
}
