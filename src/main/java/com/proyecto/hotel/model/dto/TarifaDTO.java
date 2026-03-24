package com.proyecto.hotel.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarifaDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private BigDecimal precio;
    private Long tipoHabitacionId;
    private Long tipoAlquilerId;
    private TipoHabitacionDTO tipoHabitacion;
    private TipoAlquilerDTO tipoAlquiler;
}
