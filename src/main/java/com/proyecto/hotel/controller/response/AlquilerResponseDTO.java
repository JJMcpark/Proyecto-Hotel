package com.proyecto.hotel.controller.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AlquilerResponseDTO(
    Long id,
    String numeroHabitacion,
    String nombreCliente,
    String empresaNombre,
    BigDecimal totalPagadoCaja,
    BigDecimal subTotal,
    BigDecimal pagoPendiente,
    LocalDateTime fechaIngreso,
    LocalDateTime fechaPrevista,
    String estadoAlquiler,
    String estadoHabitacion
) {}