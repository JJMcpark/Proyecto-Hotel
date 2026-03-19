package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.CuentaAlquilerDTO;
import com.proyecto.hotel.model.entities.CuentaAlquiler;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuentaAlquilerMapper {
    @Mapping(source = "alquiler.id", target = "alquilerId")
    CuentaAlquilerDTO toDTO(CuentaAlquiler cuentaAlquiler);

    @Mapping(target = "alquiler", ignore = true)
    CuentaAlquiler toEntity(CuentaAlquilerDTO cuentaAlquilerDTO);
}
