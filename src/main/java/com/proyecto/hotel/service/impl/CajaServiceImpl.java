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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

    private final UsuarioRepository usuarioRepository;
    private final MovimientoCajaRepository cajaRepository;

    @Override
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

        Long alquilerId = (m.getAlquiler() != null) ? m.getAlquiler().getId() : null;

        return new MovimientoCajaResponseDTO(
            m.getId(),
            m.getTipo().name(),
            m.getMonto(),
            m.getMetodoPago() != null ? m.getMetodoPago().name() : null,
            m.getConcepto(),
            m.getFecha(),
            m.getUsuario().getNombre(),
            habitacion,
            cliente,
            empresa,
            alquilerId
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
    public MovimientoCajaResponseDTO actualizarMonto(Long id, BigDecimal monto, com.proyecto.hotel.model.enums.MetodoPago metodoPago) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new com.proyecto.hotel.handler.BadRequestException("El monto no puede ser negativo");
        }
        MovimientoCaja movimiento = cajaRepository.findById(id)
                .orElseThrow(() -> new com.proyecto.hotel.handler.BadRequestException("Movimiento no encontrado: " + id));
        movimiento.setMonto(monto);
        if (metodoPago != null) {
            movimiento.setMetodoPago(metodoPago);
        }
        return mapToResponseDTO(cajaRepository.save(movimiento));
    }

    @Override
    @Transactional
    public MovimientoCajaResponseDTO cobrarMovimiento(Long id, com.proyecto.hotel.model.enums.MetodoPago metodoPago) {
        MovimientoCaja movimiento = cajaRepository.findById(id)
                .orElseThrow(() -> new com.proyecto.hotel.handler.BadRequestException("Movimiento no encontrado: " + id));
        if (movimiento.getTipo() != TipoMovimiento.PENDIENTE) {
            throw new com.proyecto.hotel.handler.BadRequestException("El movimiento no está en estado PENDIENTE");
        }
        movimiento.setTipo(TipoMovimiento.INGRESO);
        movimiento.setMetodoPago(metodoPago);
        return mapToResponseDTO(cajaRepository.save(movimiento));
    }

    @Override
    @Transactional
    public List<MovimientoCajaResponseDTO> cobrarLoteEmpresa(Long empresaId, java.time.LocalDate desde, java.time.LocalDate hasta, com.proyecto.hotel.model.enums.MetodoPago metodoPago) {
        if (hasta.isBefore(desde)) {
            throw new com.proyecto.hotel.handler.BadRequestException("La fecha 'hasta' no puede ser anterior a 'desde'");
        }
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(java.time.LocalTime.MAX);
        List<MovimientoCaja> pendientes = cajaRepository.findPendientesByEmpresaIdAndFechaBetween(empresaId, inicio, fin);
        if (pendientes.isEmpty()) {
            throw new com.proyecto.hotel.handler.BadRequestException("No hay movimientos pendientes para esta empresa en el período indicado");
        }
        for (MovimientoCaja m : pendientes) {
            m.setTipo(TipoMovimiento.INGRESO);
            m.setMetodoPago(metodoPago);
        }
        return cajaRepository.saveAll(pendientes).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<MovimientoCajaResponseDTO> cobrarLoteEmpresaPorIds(List<Long> ids, com.proyecto.hotel.model.enums.MetodoPago metodoPago) {
        if (ids == null || ids.isEmpty()) {
            throw new com.proyecto.hotel.handler.BadRequestException("La lista de IDs no puede estar vacía");
        }
        List<MovimientoCaja> pendientes = cajaRepository.findPendientesByIdIn(ids);
        if (pendientes.isEmpty()) {
            throw new com.proyecto.hotel.handler.BadRequestException("No hay movimientos pendientes con los IDs indicados");
        }
        for (MovimientoCaja m : pendientes) {
            m.setTipo(TipoMovimiento.INGRESO);
            m.setMetodoPago(metodoPago);
        }
        return cajaRepository.saveAll(pendientes).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> previsualizarEliminacion(java.time.LocalDate desde, java.time.LocalDate hasta) {
        long cantidad;
        String periodoDesc;

        BigDecimal totalIngresos;
        BigDecimal totalEgresos;

        if (desde != null && hasta != null) {
            if (hasta.isBefore(desde)) {
                throw new com.proyecto.hotel.handler.BadRequestException("La fecha 'hasta' no puede ser anterior a 'desde'");
            }
            LocalDateTime inicio = desde.atStartOfDay();
            LocalDateTime fin = hasta.atTime(LocalTime.MAX);
            cantidad = cajaRepository.countByFechaBetween(inicio, fin);
            totalIngresos = cajaRepository.sumIngresosByFechaBetween(inicio, fin);
            totalEgresos  = cajaRepository.sumEgresosByFechaBetween(inicio, fin);
            periodoDesc = desde + " — " + hasta;
        } else {
            cantidad = cajaRepository.count();
            totalIngresos = cajaRepository.sumIngresosAll();
            totalEgresos  = cajaRepository.sumEgresosAll();
            periodoDesc = "TODO el historial";
        }

        if (totalIngresos == null) totalIngresos = BigDecimal.ZERO;
        if (totalEgresos  == null) totalEgresos  = BigDecimal.ZERO;

        return java.util.Map.of(
            "cantidad", cantidad,
            "totalIngresos", totalIngresos,
            "totalEgresos", totalEgresos,
            "periodo", periodoDesc
        );
    }

    @Override
    @Transactional
    public int eliminarMovimientos(java.time.LocalDate desde, java.time.LocalDate hasta, String adminDni) {
        int deleted;
        String periodoDesc;

        if (desde != null && hasta != null) {
            if (hasta.isBefore(desde)) {
                throw new com.proyecto.hotel.handler.BadRequestException("La fecha 'hasta' no puede ser anterior a 'desde'");
            }
            LocalDateTime inicio = desde.atStartOfDay();
            LocalDateTime fin = hasta.atTime(LocalTime.MAX);
            deleted = cajaRepository.deleteByFechaBetween(inicio, fin);
            periodoDesc = desde + " — " + hasta;
        } else {
            deleted = cajaRepository.deleteAllMovimientos();
            periodoDesc = "TODO";
        }

        log.warn("AUDIT: admin='{}' eliminó movimientos de caja. Período: {}. Registros borrados: {}", adminDni, periodoDesc, deleted);
        return deleted;
    }
}
