package com.proyecto.hotel.controller.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record IncrementoTarifaRequestDTO(
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal porcentaje
) {}
