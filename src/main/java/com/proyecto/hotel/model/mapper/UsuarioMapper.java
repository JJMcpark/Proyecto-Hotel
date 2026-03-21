package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.UsuarioDTO;
import com.proyecto.hotel.model.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = {TipoDocumentoMapper.class, RolMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UsuarioMapper {
    
    UsuarioDTO toDTO(Usuario usuario);

    @Mapping(target = "password", ignore = true)
    Usuario toEntity(UsuarioDTO usuarioDTO);
    
    void updateEntityFromDTO(UsuarioDTO dto, @MappingTarget Usuario entity);
}
