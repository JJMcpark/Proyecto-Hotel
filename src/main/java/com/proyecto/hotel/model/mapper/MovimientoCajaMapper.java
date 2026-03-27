package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.MovimientoCajaDTO;
import com.proyecto.hotel.model.entities.MovimientoCaja;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class, AlquilerMapper.class})
public interface MovimientoCajaMapper {
    MovimientoCajaDTO toDTO(MovimientoCaja movimientoCaja);
    MovimientoCaja toEntity(MovimientoCajaDTO movimientoCajaDTO);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "alquiler", ignore = true)
    void updateEntityFromDTO(MovimientoCajaDTO dto, @MappingTarget MovimientoCaja entity);
}
