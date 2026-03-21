package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.MovimientoCaja;
import com.proyecto.hotel.model.enums.MetodoPago;
import com.proyecto.hotel.model.enums.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findByTipo(TipoMovimiento tipo);
    List<MovimientoCaja> findByMetodoPago(MetodoPago metodoPago);
    List<MovimientoCaja> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<MovimientoCaja> findByTipoAndFechaBetween(TipoMovimiento tipo, LocalDateTime inicio, LocalDateTime fin);
    List<MovimientoCaja> findByUsuarioId(Long usuarioId);
}
