package com.proyecto.hotel.model.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaAlquilerDTO {
    private Long id;
    private String descripcion;
    private BigDecimal precioUnit;
    private Integer cantidad;
    private BigDecimal subTotal;
    private String estado;
    private Long alquilerId;
}
