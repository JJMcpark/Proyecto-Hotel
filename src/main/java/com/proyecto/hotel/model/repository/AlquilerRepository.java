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

import java.time.LocalDateTime;
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

    boolean existsByEmpresaIdAndEstado(Long empresaId, EstadoAlquiler estado);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Alquiler a SET a.empresa = :nueva WHERE a.empresa.id = :oldId")
    int reasignarEmpresa(@Param("oldId") Long oldId, @Param("nueva") com.proyecto.hotel.model.entities.Empresa nueva);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE alquiler SET id_empresa = :empresaId WHERE id_cliente = :clienteId AND estado = 'ACTIVO'", nativeQuery = true)
    int actualizarEmpresaEnAlquileresActivos(@Param("clienteId") Long clienteId,
                                              @Param("empresaId") Long empresaId);

    @Modifying
    @Query(value = "DELETE FROM alquiler_cliente WHERE id_cliente = :clienteId", nativeQuery = true)
    int eliminarHuespedPorClienteId(@Param("clienteId") Long clienteId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Alquiler a WHERE a.cliente.id = :clienteId AND a.estado = :estado")
    int eliminarPorClienteIdYEstado(@Param("clienteId") Long clienteId, @Param("estado") EstadoAlquiler estado);

    long countByEstado(EstadoAlquiler estado);

    long countByEstadoAndFechaIngresoBetween(EstadoAlquiler estado, LocalDateTime desde, LocalDateTime hasta);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Alquiler a WHERE a.estado = :estado")
    int deleteByEstado(@Param("estado") EstadoAlquiler estado);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Alquiler a WHERE a.estado = :estado AND a.fechaIngreso BETWEEN :desde AND :hasta")
    int deleteByEstadoAndFechaIngresoBetween(@Param("estado") EstadoAlquiler estado, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT a.id FROM Alquiler a WHERE a.estado = :estado")
    List<Long> findIdsByEstado(@Param("estado") EstadoAlquiler estado);

    @Query("SELECT a.id FROM Alquiler a WHERE a.estado = :estado AND a.fechaIngreso BETWEEN :desde AND :hasta")
    List<Long> findIdsByEstadoAndFechaIngresoBetween(@Param("estado") EstadoAlquiler estado, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    boolean existsByHabitacionId(Long habitacionId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Alquiler a WHERE a.tarifa.tipoAlquiler.id = :tipoAlquilerId AND a.estado = :estado")
    boolean existsByTarifa_TipoAlquilerIdAndEstado(@Param("tipoAlquilerId") Long tipoAlquilerId, @Param("estado") EstadoAlquiler estado);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Alquiler a WHERE a.tarifa.tipoHabitacion.id = :tipoHabitacionId AND a.estado = :estado")
    boolean existsByTarifa_TipoHabitacionIdAndEstado(@Param("tipoHabitacionId") Long tipoHabitacionId, @Param("estado") EstadoAlquiler estado);
}
