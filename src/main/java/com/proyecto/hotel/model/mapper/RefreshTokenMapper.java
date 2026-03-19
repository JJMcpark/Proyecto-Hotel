package com.proyecto.hotel.model.mapper;

import com.proyecto.hotel.model.dto.RefreshTokenDTO;
import com.proyecto.hotel.model.entities.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mapping(source = "usuario.id", target = "usuarioId")
    RefreshTokenDTO toDTO(RefreshToken refreshToken);

    @Mapping(target = "usuario", ignore = true)
    RefreshToken toEntity(RefreshTokenDTO refreshTokenDTO);
}
