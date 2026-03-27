package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByRuc(String ruc);
    boolean existsByRuc(String ruc);
}
