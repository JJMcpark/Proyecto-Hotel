package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.TarifaDTO;
import com.proyecto.hotel.model.entities.Tarifa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TipoHabitacionMapper.class, TipoAlquilerMapper.class})
public interface TarifaMapper {
    TarifaDTO toDTO(Tarifa tarifa);
    Tarifa toEntity(TarifaDTO tarifaDTO);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoHabitacion", ignore = true)
    @Mapping(target = "tipoAlquiler", ignore = true)
    void updateEntityFromDTO(TarifaDTO dto, @MappingTarget Tarifa entity);
}
