package com.proyecto.hotel.model.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

import lombok.*;
import com.proyecto.hotel.model.enums.EstadoCuenta;

@Entity
@Table(name = "cuenta_alquiler")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaAlquiler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(name = "precio_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnit;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "sub_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal subTotal;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoCuenta estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alquiler")
    private Alquiler alquiler;

    @PrePersist
    private void prePersist() {
        if (this.estado == null) {
            this.estado = EstadoCuenta.PENDIENTE;
        }
        if (this.cantidad == null) {
            this.cantidad = 1;
        }
    }

}
