package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.RolDTO;
import com.proyecto.hotel.model.entities.Rol;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RolMapper {
    RolDTO toDTO(Rol rol);
    Rol toEntity(RolDTO rolDTO);
}
