package com.proyecto.hotel.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.PrePersist;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("token_type")
    private String tokenType;
    
    private String numDocumento;
    
    private String nombre;
    
    private String rol;

    @PrePersist
    private void prePersist() {
        if (this.tokenType == null) {
            this.tokenType = "Bearer";
        }
    }
}
