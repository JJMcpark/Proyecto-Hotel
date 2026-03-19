package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.ClienteDTO;
import com.proyecto.hotel.model.entities.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TipoDocumentoMapper.class})
public interface ClienteMapper {
    ClienteDTO toDTO(Cliente cliente);
    Cliente toEntity(ClienteDTO clienteDTO);
}
