package com.proyecto.hotel.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private BigDecimal precio;
    @NotNull(message = "El tipo de habitación es obligatorio")
    private Long tipoHabitacionId;
    @NotNull(message = "El tipo de alquiler es obligatorio")
    private Long tipoAlquilerId;
    private TipoHabitacionDTO tipoHabitacion;
    private TipoAlquilerDTO tipoAlquiler;
}
