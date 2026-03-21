package com.proyecto.hotel.controller.request;

import com.proyecto.hotel.model.enums.MetodoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record GastoRequestDTO(
    @NotBlank String concepto,
    @NotNull @Positive BigDecimal monto,
    @NotNull MetodoPago metodoPago
) {}