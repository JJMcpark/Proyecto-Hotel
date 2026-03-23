package com.proyecto.hotel.controller.response;

import java.math.BigDecimal;
import java.util.List;

public record ResumenCajaDTO(
    BigDecimal totalIngresos,
    BigDecimal totalEgresos,
    BigDecimal balance,
    int cantidadMovimientos,
    List<MovimientoCajaResponseDTO> movimientos
) {}
