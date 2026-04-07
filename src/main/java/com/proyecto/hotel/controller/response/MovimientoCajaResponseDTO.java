package com.proyecto.hotel.controller.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoCajaResponseDTO(
    Long id,
    String tipo,
    BigDecimal monto,
    String metodoPago,
    String concepto,
    LocalDateTime fecha,
    String nombreUsuario,
    String numeroHabitacion,
    String nombreCliente,
    String nombreEmpresa,
    Long alquilerId
) {}
