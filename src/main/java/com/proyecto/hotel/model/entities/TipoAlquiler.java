package com.proyecto.hotel.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_alquiler")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoAlquiler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;
}
