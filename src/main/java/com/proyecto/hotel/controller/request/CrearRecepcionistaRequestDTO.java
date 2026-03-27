package com.proyecto.hotel.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearRecepcionistaRequestDTO {
    @NotBlank(message = "nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "numDocumento es obligatorio")
    private String numDocumento;

    @NotBlank(message = "password es obligatorio")
    private String password;

    private String telefono;

    @NotBlank(message = "tipoDocumento es obligatorio")
    private String tipoDocumento;
}
