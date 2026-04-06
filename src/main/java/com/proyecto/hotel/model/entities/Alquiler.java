package com.proyecto.hotel.model.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import com.proyecto.hotel.model.enums.EstadoAlquiler;

@Entity
@Table(name = "alquiler")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alquiler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_prevista", nullable = false)
    private LocalDateTime fechaPrevista;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "precio_fijado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioFijado;

    @Column(name = "cant_tiempo", nullable = false)
    private Integer cantTiempo;

    @Column(name = "pago_pendiente", nullable = false, precision = 10, scale = 2)
    private BigDecimal pagoPendiente;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoAlquiler estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_habitacion", nullable = false)
    private Habitacion habitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarifa", nullable = false)
    private Tarifa tarifa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "alquiler_cliente",
        joinColumns = @JoinColumn(name = "id_alquiler"),
        inverseJoinColumns = @JoinColumn(name = "id_cliente")
    )
    private List<Cliente> huespedes = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        if (this.fechaIngreso == null) {
            this.fechaIngreso = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoAlquiler.ACTIVO;
        }
    }
}
