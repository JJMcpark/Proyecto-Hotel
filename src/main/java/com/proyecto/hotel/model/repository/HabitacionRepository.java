package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.Habitacion;
import com.proyecto.hotel.model.enums.EstadoHabitacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    @EntityGraph(attributePaths = {"tipoHabitacion"})
    List<Habitacion> findByEstado(EstadoHabitacion estado);

    @EntityGraph(attributePaths = {"tipoHabitacion"})
    List<Habitacion> findByTipoHabitacionNombre(String tipo);

    boolean existsByTipoHabitacionId(Long tipoHabitacionId);
}
