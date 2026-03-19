package com.proyecto.hotel.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.hotel.model.entities.Alquiler;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
    List<Alquiler> findByEstado(String estado);
    List<Alquiler> findByClienteId(Long clienteId);
    List<Alquiler> findByHabitacionId(Long habitacionId);
    List<Alquiler> findByFechaIngresoBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Alquiler> findByEstadoAndFechaPrevistaBefore(String estado, LocalDateTime fecha);
}
