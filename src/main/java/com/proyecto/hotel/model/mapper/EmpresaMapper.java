package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.EmpresaDTO;
import com.proyecto.hotel.model.entities.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmpresaMapper {
    EmpresaDTO toDTO(Empresa empresa);
    Empresa toEntity(EmpresaDTO empresaDTO);
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(EmpresaDTO dto, @MappingTarget Empresa entity);
}
