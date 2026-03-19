package com.proyecto.hotel.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenDTO {
    private Long id;
    private String refreshToken;
    private Boolean isLoggedOut;
    private Long usuarioId;
}
