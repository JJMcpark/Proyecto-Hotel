package com.proyecto.hotel.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import com.proyecto.hotel.model.enums.MetodoPago;

public record CheckInRequestDTO(
    @NotNull Long idCliente,
    @NotNull Long idHabitacion,
    @NotNull Long idTipoAlquiler, // ID de "POR HORA" o "POR NOCHE"
    @Positive int cantTiempo,      // 3 horas o 2 noches
    BigDecimal adelanto,
    MetodoPago metodoPago
) {}