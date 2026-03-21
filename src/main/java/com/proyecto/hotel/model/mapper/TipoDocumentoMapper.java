package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.TipoDocumentoDTO;
import com.proyecto.hotel.model.entities.TipoDocumento;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TipoDocumentoMapper {
    TipoDocumentoDTO toDTO(TipoDocumento tipoDocumento);
    TipoDocumento toEntity(TipoDocumentoDTO tipoDocumentoDTO);
    void updateEntityFromDTO(TipoDocumentoDTO dto, @MappingTarget TipoDocumento entity);
}
