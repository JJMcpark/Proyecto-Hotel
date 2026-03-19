package com.proyecto.hotel.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false)
    private String refreshToken;

    @Column(name = "is_logged_out")
    private Boolean isLoggedOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @PrePersist
    private void prePersist() {
        if (this.isLoggedOut == null) {
            this.isLoggedOut = false;
        }
    }
}
