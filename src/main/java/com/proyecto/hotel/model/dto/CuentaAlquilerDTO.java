package com.proyecto.hotel.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaAlquilerDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String descripcion;
    private BigDecimal precioUnit;
    private Integer cantidad;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal subTotal;
    private String estado;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long alquilerId;
}
