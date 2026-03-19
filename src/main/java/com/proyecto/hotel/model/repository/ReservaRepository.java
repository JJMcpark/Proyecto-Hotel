package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByHabitacionIdAndEstadoIn(Long habitacionId, List<String> estados);
    List<Reserva> findByFechaEntradaBetweenOrFechaSalidaBetween(LocalDate inicio, LocalDate fin, LocalDate inicio2, LocalDate fin2);
}
