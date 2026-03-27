package com.proyecto.hotel.model.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoCajaDTO {
    private Long id;
    private String tipo;
    private BigDecimal monto;
    private String metodoPago;
    private String concepto;
    private LocalDateTime fecha;
    private UsuarioDTO usuario;
    private AlquilerDTO alquiler;
}
