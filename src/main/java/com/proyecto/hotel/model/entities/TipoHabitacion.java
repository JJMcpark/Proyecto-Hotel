package com.proyecto.hotel.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_habitacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoHabitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;
}
