package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.CuentaAlquiler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaAlquilerRepository extends JpaRepository<CuentaAlquiler, Long> {
    List<CuentaAlquiler> findByAlquilerId(Long alquilerId);
    List<CuentaAlquiler> findByEstado(String estado);
    List<CuentaAlquiler> findByAlquilerIdAndEstado(Long alquilerId, String estado);
}
