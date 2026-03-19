package com.proyecto.hotel.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitacionDTO {
    private Long id;
    private Integer piso;
    private String numero;
    private String descripcion;
    private String estado;
    private TipoHabitacionDTO tipoHabitacion;
}
