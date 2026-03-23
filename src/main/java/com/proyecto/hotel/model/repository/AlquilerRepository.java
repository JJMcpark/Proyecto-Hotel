package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.enums.EstadoAlquiler;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.hotel.model.entities.Alquiler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
    @EntityGraph(attributePaths = {"cliente", "habitacion", "tarifa", "usuario", "empresa"})
    List<Alquiler> findByEstado(EstadoAlquiler estado);

    @EntityGraph(attributePaths = {"cliente", "habitacion", "tarifa", "usuario", "empresa"})
    Optional<Alquiler> findAlquilerById(Long id);

    List<Alquiler> findByClienteId(Long clienteId);
    List<Alquiler> findByHabitacionId(Long habitacionId);
    List<Alquiler> findByFechaIngresoBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Alquiler> findByEstadoAndFechaPrevistaBefore(EstadoAlquiler estado, LocalDateTime fecha);
}
