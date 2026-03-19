package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    Optional<Tarifa> findByTipoHabitacionIdAndTipoAlquilerId(Long tipoHabitacionId, Long tipoAlquilerId);
    List<Tarifa> findByTipoHabitacionId(Long tipoHabitacionId);
    List<Tarifa> findByTipoAlquilerId(Long tipoAlquilerId);
}
