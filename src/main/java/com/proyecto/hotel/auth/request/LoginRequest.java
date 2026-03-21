package com.proyecto.hotel.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "num_documento es obligatorio")
    @JsonProperty("num_documento")
    private String numDocumento;

    @NotBlank(message = "password es obligatorio")
    private String password;

    // Opcional para validación de rol durante login (e.g., ADMINISTRADOR / RECEPCIONISTA)
    private String rol;
}
