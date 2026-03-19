package com.proyecto.hotel.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "habitacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer piso;

    @Column(nullable = false, unique = true, length = 10)
    private String numero;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 20)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_habitacion")
    private TipoHabitacion tipoHabitacion;

    @PrePersist
    private void prePersist() {
        if (this.estado == null) {
            this.estado = "DISPONIBLE";
        }
    }
}
