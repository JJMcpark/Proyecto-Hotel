package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.UsuarioDTO;
import com.proyecto.hotel.model.entities.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TipoDocumentoMapper.class, RolMapper.class})
public interface UsuarioMapper {
    UsuarioDTO toDTO(Usuario usuario);
    Usuario toEntity(UsuarioDTO usuarioDTO);
}
