package com.proyecto.hotel.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String ruc;
    private String telefono;
}
