package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.MovimientoCaja;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {

    @EntityGraph(attributePaths = {"usuario", "alquiler", "alquiler.habitacion", "alquiler.cliente", "alquiler.cliente.empresa"})
    List<MovimientoCaja> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.alquiler.id = :alquilerId AND m.tipo = com.proyecto.hotel.model.enums.TipoMovimiento.INGRESO")
    java.math.BigDecimal sumNetByAlquilerId(@Param("alquilerId") Long alquilerId);

    @Query("SELECT m.alquiler.id, COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.alquiler.id IN :ids AND m.tipo = com.proyecto.hotel.model.enums.TipoMovimiento.INGRESO GROUP BY m.alquiler.id")
    List<Object[]> sumNetByAlquilerIds(@Param("ids") List<Long> ids);

    @EntityGraph(attributePaths = {"usuario", "alquiler", "alquiler.habitacion", "alquiler.cliente", "alquiler.cliente.empresa"})
    List<MovimientoCaja> findByAlquilerId(Long alquilerId);

    @EntityGraph(attributePaths = {"usuario", "alquiler", "alquiler.habitacion", "alquiler.cliente", "alquiler.cliente.empresa"})
    @Query("SELECT m FROM MovimientoCaja m WHERE m.tipo = com.proyecto.hotel.model.enums.TipoMovimiento.PENDIENTE " +
           "AND m.alquiler.empresa.id = :empresaId " +
           "AND m.fecha BETWEEN :inicio AND :fin")
    List<MovimientoCaja> findPendientesByEmpresaIdAndFechaBetween(
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
