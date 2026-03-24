package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.enums.EstadoAlquiler;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
=======
import com.proyecto.hotel.model.entities.Cliente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
>>>>>>> f942943 (Actualización 24/03)
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
<<<<<<< HEAD
=======
    long countByClienteId(Long clienteId);
    long countByClienteIdAndEstado(Long clienteId, EstadoAlquiler estado);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Alquiler a SET a.cliente = :nuevoCliente WHERE a.cliente.id = :clienteId AND a.estado = :estado")
    int reasignarClientePorEstado(@Param("clienteId") Long clienteId,
                                 @Param("nuevoCliente") Cliente nuevoCliente,
                                 @Param("estado") EstadoAlquiler estado);
>>>>>>> f942943 (Actualización 24/03)
    List<Alquiler> findByHabitacionId(Long habitacionId);
    List<Alquiler> findByFechaIngresoBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Alquiler> findByEstadoAndFechaPrevistaBefore(EstadoAlquiler estado, LocalDateTime fecha);
}
