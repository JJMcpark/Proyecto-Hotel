package com.proyecto.hotel.model.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarifaDTO {
    private Long id;
    private BigDecimal precio;
    private TipoHabitacionDTO tipoHabitacion;
    private TipoAlquilerDTO tipoAlquiler;
}
