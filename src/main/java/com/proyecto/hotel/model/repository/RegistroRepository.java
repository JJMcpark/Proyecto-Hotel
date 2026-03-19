package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {
    List<Registro> findByFechaMovBetween(LocalDateTime inicio, LocalDateTime fin);
}
