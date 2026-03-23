package com.proyecto.hotel.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarPasswordRequestDTO {
    @NotBlank(message = "currentPassword es obligatorio")
    private String currentPassword;

    @NotBlank(message = "newPassword es obligatorio")
    private String newPassword;
}
