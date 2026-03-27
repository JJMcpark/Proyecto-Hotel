package com.proyecto.hotel.controller.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ActualizarMontosAlquilerRequestDTO(
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal subTotal,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal pagoPendiente
) {}
