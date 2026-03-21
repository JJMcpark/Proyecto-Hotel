package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    List<Habitacion> findByEstado(String estado);
    List<Habitacion> findByTipoHabitacionNombre(String tipo);
}
