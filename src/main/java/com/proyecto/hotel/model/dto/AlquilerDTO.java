package com.proyecto.hotel.model.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlquilerDTO {
    private Long id;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaPrevista;
    private LocalDateTime fechaSalida;
    private BigDecimal precioFijado;
    private Integer cantTiempo;
    private BigDecimal pagoPendiente;
    private String estado;
    private ClienteDTO cliente;
    private HabitacionDTO habitacion;
    private TarifaDTO tarifa;
    private UsuarioDTO usuario;
    private EmpresaDTO empresa;
}
