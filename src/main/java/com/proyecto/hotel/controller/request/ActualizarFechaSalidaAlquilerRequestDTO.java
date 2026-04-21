package com.proyecto.hotel.controller.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ActualizarFechaSalidaAlquilerRequestDTO(
    @NotNull LocalDateTime fechaSalida
) {}
