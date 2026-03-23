package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.TipoAlquilerDTO;
import com.proyecto.hotel.model.entities.TipoAlquiler;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TipoAlquilerMapper {
    TipoAlquilerDTO toDTO(TipoAlquiler tipoAlquiler);
    TipoAlquiler toEntity(TipoAlquilerDTO tipoAlquilerDTO);
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(TipoAlquilerDTO dto, @MappingTarget TipoAlquiler entity);
}
