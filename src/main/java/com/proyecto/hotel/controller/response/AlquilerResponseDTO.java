package com.proyecto.hotel.controller.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AlquilerResponseDTO(
    Long id,
    String numeroHabitacion,
    String nombreCliente,
    String empresaNombre,
    String tipoAlquilerNombre,
    BigDecimal totalPagadoCaja,
    BigDecimal subTotal,
    BigDecimal pagoPendiente,
    LocalDateTime fechaIngreso,
    LocalDateTime fechaPrevista,
    LocalDateTime fechaSalida,
    String estadoAlquiler,
    String estadoHabitacion,
    List<String> huespedes
) {}