package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.enums.EstadoAlquiler;
import com.proyecto.hotel.model.entities.Cliente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.hotel.model.entities.Alquiler;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
    @EntityGraph(attributePaths = {"cliente", "habitacion", "tarifa", "usuario", "empresa", "huespedes"})
    List<Alquiler> findByEstado(EstadoAlquiler estado);

    @EntityGraph(attributePaths = {"cliente", "habitacion", "tarifa", "usuario", "empresa", "huespedes"})
    Optional<Alquiler> findAlquilerById(Long id);

    List<Alquiler> findByClienteId(Long clienteId);
    long countByClienteId(Long clienteId);
    long countByClienteIdAndEstado(Long clienteId, EstadoAlquiler estado);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Alquiler a SET a.cliente = :nuevoCliente WHERE a.cliente.id = :clienteId AND a.estado = :estado")
    int reasignarClientePorEstado(@Param("clienteId") Long clienteId,
                                 @Param("nuevoCliente") Cliente nuevoCliente,
                                 @Param("estado") EstadoAlquiler estado);
    @Query("SELECT COUNT(a) FROM Alquiler a WHERE a.id = :alquilerId AND a.cliente.empresa IS NOT NULL")
    long countEmpresaAlquilerById(@Param("alquilerId") Long alquilerId);

    @EntityGraph(attributePaths = {"cliente", "habitacion", "tarifa", "usuario", "empresa", "huespedes"})
    @Query("SELECT a FROM Alquiler a WHERE a.habitacion.id = :habitacionId " +
           "AND YEAR(a.fechaIngreso) = :anio AND MONTH(a.fechaIngreso) = :mes " +
           "ORDER BY a.fechaIngreso ASC")
    List<Alquiler> findByHabitacionIdAndMesAnio(@Param("habitacionId") Long habitacionId,
                                                 @Param("mes") int mes,
                                                 @Param("anio") int anio);

    @EntityGraph(attributePaths = {"cliente", "habitacion", "tarifa", "usuario", "empresa", "huespedes"})
    Optional<Alquiler> findAlquilerWithHuespedesById(Long id);
}
