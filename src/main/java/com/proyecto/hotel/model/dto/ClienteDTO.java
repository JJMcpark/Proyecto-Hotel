package com.proyecto.hotel.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "El número de documento es obligatorio")
    private String numDocumento;
    private String telefono;
    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumentoDTO tipoDocumento;
    private Long empresaId;
    private String empresaNombre;
}
