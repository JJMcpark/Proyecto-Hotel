package com.proyecto.hotel.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String numDocumento;
    private String telefono;
    private TipoDocumentoDTO tipoDocumento;
    private RolDTO rol;
}
