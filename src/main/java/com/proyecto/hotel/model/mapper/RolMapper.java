package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.RolDTO;
import com.proyecto.hotel.model.entities.Rol;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RolMapper {
    RolDTO toDTO(Rol rol);
    Rol toEntity(RolDTO rolDTO);
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(RolDTO dto, @MappingTarget Rol entity);
}
