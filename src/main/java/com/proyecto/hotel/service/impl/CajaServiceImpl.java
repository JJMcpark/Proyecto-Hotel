package com.proyecto.hotel.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.hotel.controller.request.GastoRequestDTO;
import com.proyecto.hotel.controller.response.MovimientoCajaResponseDTO;
import com.proyecto.hotel.controller.response.ResumenCajaDTO;
import com.proyecto.hotel.model.entities.MovimientoCaja;
import com.proyecto.hotel.model.enums.TipoMovimiento;
import com.proyecto.hotel.model.repository.UsuarioRepository;
import com.proyecto.hotel.model.repository.MovimientoCajaRepository;
import com.proyecto.hotel.service.CajaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

    private final UsuarioRepository usuarioRepository;
    private final MovimientoCajaRepository cajaRepository;

    @Transactional
    public MovimientoCajaResponseDTO registrarMovimiento(GastoRequestDTO dto, TipoMovimiento tipo, String dniUsuario) {
        // 1. El Observador: Quién está operando la caja
        var usuario = usuarioRepository.findByNumDocumento(dniUsuario)
                .orElseThrow(() -> new com.proyecto.hotel.handler.BadRequestException("Usuario no encontrado: " + dniUsuario));

        // 2. Registro universal de movimiento
        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setConcepto(dto.concepto());
        movimiento.setMonto(dto.monto());
        movimiento.setMetodoPago(dto.metodoPago());
        movimiento.setTipo(tipo); // INGRESO o EGRESO (Enum)
        movimiento.setUsuario(usuario);
        movimiento.setAlquiler(null); // Para gastos generales o compras de stock

        var guardado = cajaRepository.save(movimiento);
        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponseDTO> listarMovimientosHoy() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = LocalDate.now().atTime(LocalTime.MAX);
        
        return cajaRepository.findByFechaBetween(inicio, fin)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponseDTO> listarMovimientosPorRango(LocalDate desde, LocalDate hasta) {
        if (hasta.isBefore(desde)) {
            throw new com.proyecto.hotel.handler.BadRequestException("La fecha 'hasta' no puede ser anterior a la fecha 'desde'");
        }

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);

        return cajaRepository.findByFechaBetween(inicio, fin)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponseDTO> listarMovimientosPorAlquiler(Long alquilerId) {
        return cajaRepository.findByAlquilerId(alquilerId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private MovimientoCajaResponseDTO mapToResponseDTO(MovimientoCaja m) {
        String habitacion = (m.getAlquiler() != null) 
            ? m.getAlquiler().getHabitacion().getNumero() 
            : "N/A";
            
        String cliente = (m.getAlquiler() != null) 
            ? m.getAlquiler().getCliente().getNombre() 
            : "GENERAL";

        String empresa = (m.getAlquiler() != null
                && m.getAlquiler().getCliente() != null
                && m.getAlquiler().getCliente().getEmpresa() != null)
            ? m.getAlquiler().getCliente().getEmpresa().getNombre()
            : "—";

        return new MovimientoCajaResponseDTO(
            m.getId(),
            m.getTipo().name(),
            m.getMonto(),
            m.getMetodoPago().name(),
            m.getConcepto(),
            m.getFecha(),
            m.getUsuario().getNombre(),
            habitacion,
            cliente,
            empresa
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenCajaDTO obtenerResumen(LocalDate desde, LocalDate hasta) {
        if (hasta.isBefore(desde)) {
            throw new com.proyecto.hotel.handler.BadRequestException("La fecha 'hasta' no puede ser anterior a la fecha 'desde'");
        }

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);

        List<MovimientoCajaResponseDTO> movimientos = cajaRepository.findByFechaBetween(inicio, fin)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();

        BigDecimal totalIngresos = movimientos.stream()
                .filter(m -> "INGRESO".equals(m.tipo()))
                .map(MovimientoCajaResponseDTO::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEgresos = movimientos.stream()
                .filter(m -> "EGRESO".equals(m.tipo()))
                .map(MovimientoCajaResponseDTO::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ResumenCajaDTO(
                totalIngresos,
                totalEgresos,
                totalIngresos.subtract(totalEgresos),
                movimientos.size(),
                movimientos
        );
    }

    @Override
    @Transactional
    public MovimientoCajaResponseDTO actualizarMonto(Long id, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new com.proyecto.hotel.handler.BadRequestException("El monto debe ser mayor a 0");
        }
        MovimientoCaja movimiento = cajaRepository.findById(id)
                .orElseThrow(() -> new com.proyecto.hotel.handler.BadRequestException("Movimiento no encontrado: " + id));
        movimiento.setMonto(monto);
        return mapToResponseDTO(cajaRepository.save(movimiento));
    }
}
