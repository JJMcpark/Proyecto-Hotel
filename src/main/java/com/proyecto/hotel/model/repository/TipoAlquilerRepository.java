package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.TipoAlquiler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoAlquilerRepository extends JpaRepository<TipoAlquiler, Long> {
    Optional<TipoAlquiler> findByNombre(String nombre);
}
