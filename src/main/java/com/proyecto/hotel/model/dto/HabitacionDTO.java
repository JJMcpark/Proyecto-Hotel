package com.proyecto.hotel.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitacionDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotNull(message = "El piso es obligatorio")
    private Integer piso;
    @NotBlank(message = "El número de habitación es obligatorio")
    private String numero;
    private String descripcion;
    private String estado;
    @NotNull(message = "El tipo de habitación es obligatorio")
    private TipoHabitacionDTO tipoHabitacion;
}
