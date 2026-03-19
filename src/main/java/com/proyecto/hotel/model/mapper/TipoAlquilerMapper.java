package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.TipoAlquilerDTO;
import com.proyecto.hotel.model.entities.TipoAlquiler;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TipoAlquilerMapper {
    TipoAlquilerDTO toDTO(TipoAlquiler tipoAlquiler);
    TipoAlquiler toEntity(TipoAlquilerDTO tipoAlquilerDTO);
}
