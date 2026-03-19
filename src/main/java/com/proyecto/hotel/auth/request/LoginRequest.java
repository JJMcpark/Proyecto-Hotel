package com.proyecto.hotel.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    
    @JsonProperty("num_documento")
    private String numDocumento;
    
    private String password;
}
