package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Long> {
    Optional<TipoHabitacion> findByNombre(String nombre);
}
