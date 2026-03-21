package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.AlquilerDTO;
import com.proyecto.hotel.model.entities.Alquiler;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {ClienteMapper.class, HabitacionMapper.class, UsuarioMapper.class, TarifaMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlquilerMapper {
    AlquilerDTO toDTO(Alquiler alquiler);
    Alquiler toEntity(AlquilerDTO alquilerDTO);
    void updateEntityFromDTO(AlquilerDTO dto, @MappingTarget Alquiler entity);
}
