package com.proyecto.hotel.model.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "tarifa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_habitacion")
    private TipoHabitacion tipoHabitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_alquiler")
    private TipoAlquiler tipoAlquiler;
}
