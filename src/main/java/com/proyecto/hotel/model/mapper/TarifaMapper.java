package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.TarifaDTO;
import com.proyecto.hotel.model.entities.Tarifa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TipoHabitacionMapper.class, TipoAlquilerMapper.class})
public interface TarifaMapper {
    TarifaDTO toDTO(Tarifa tarifa);
    Tarifa toEntity(TarifaDTO tarifaDTO);
    void updateEntityFromDTO(TarifaDTO dto, @MappingTarget Tarifa entity);
}
