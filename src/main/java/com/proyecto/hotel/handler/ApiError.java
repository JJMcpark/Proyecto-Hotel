package com.proyecto.hotel.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ApiError {

    private LocalDateTime timestamp;
    private int status;  // Código HTTP
    private String error;  // Tipo de error
    private String message;  // Mensaje detallado
    private String path;  // Ruta que causó el error
}
