package com.proyecto.hotel.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarRecepcionistaRequestDTO {
    @NotBlank(message = "nombre es obligatorio")
    private String nombre;

    private String telefono;

    @NotBlank(message = "tipoDocumento es obligatorio")
    private String tipoDocumento;
}
