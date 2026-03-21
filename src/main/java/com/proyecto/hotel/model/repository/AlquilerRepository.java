package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.enums.EstadoAlquiler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.hotel.model.entities.Alquiler;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
    List<Alquiler> findByEstado(EstadoAlquiler estado);
    List<Alquiler> findByClienteId(Long clienteId);
    List<Alquiler> findByHabitacionId(Long habitacionId);
    List<Alquiler> findByFechaIngresoBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Alquiler> findByEstadoAndFechaPrevistaBefore(EstadoAlquiler estado, LocalDateTime fecha);
}
