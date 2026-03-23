package com.proyecto.hotel.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarPerfilRequestDTO {
    @NotBlank(message = "nombre es obligatorio")
    private String nombre;

    private String telefono;

    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "numDocumento debe tener entre 4 y 20 caracteres alfanuméricos")
    private String numDocumento;

    private String tipoDocumento;
}
