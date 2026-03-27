package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.ClienteDTO;
import com.proyecto.hotel.model.entities.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {TipoDocumentoMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClienteMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(source = "empresa.nombre", target = "empresaNombre")
    ClienteDTO toDTO(Cliente cliente);

    @Mapping(target = "empresa", ignore = true)
    Cliente toEntity(ClienteDTO clienteDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "tipoDocumento", ignore = true)
    void updateEntityFromDTO(ClienteDTO dto, @MappingTarget Cliente entity);
}
