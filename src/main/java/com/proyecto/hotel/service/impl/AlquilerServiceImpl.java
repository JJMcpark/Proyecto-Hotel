package com.proyecto.hotel.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.hotel.controller.request.CheckInRequestDTO;
import com.proyecto.hotel.controller.response.AlquilerResponseDTO;
import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.entities.Alquiler;
import com.proyecto.hotel.model.entities.Cliente;
import com.proyecto.hotel.model.entities.MovimientoCaja;
import com.proyecto.hotel.model.entities.Usuario;
import com.proyecto.hotel.model.enums.EstadoAlquiler;
import com.proyecto.hotel.model.enums.EstadoHabitacion;
import com.proyecto.hotel.model.enums.MetodoPago;
import com.proyecto.hotel.model.enums.TipoMovimiento;
import com.proyecto.hotel.model.repository.AlquilerRepository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.proyecto.hotel.model.entities.CuentaAlquiler;
import com.proyecto.hotel.model.enums.EstadoCuenta;
import com.proyecto.hotel.model.repository.ClienteRepository;
import com.proyecto.hotel.model.repository.CuentaAlquilerRepository;
import com.proyecto.hotel.model.repository.HabitacionRepository;
import com.proyecto.hotel.model.repository.MovimientoCajaRepository;
import com.proyecto.hotel.model.repository.TarifaRepository;
import com.proyecto.hotel.model.repository.UsuarioRepository;
import com.proyecto.hotel.service.AlquilerService;
import com.proyecto.hotel.model.repository.TipoAlquilerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlquilerServiceImpl implements AlquilerService {

    private final AlquilerRepository alquilerRepository;
    private final HabitacionRepository habitacionRepository;
    private final TipoAlquilerRepository tipoAlquilerRepository;
    private final TarifaRepository tarifaRepository;
    private final ClienteRepository clienteRepository;
    private final MovimientoCajaRepository cajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuentaAlquilerRepository cuentaAlquilerRepository;

    @Override
    @Transactional
    public AlquilerResponseDTO registrarCheckIn(CheckInRequestDTO dto, String dniUsuario) {

        var habitacion = habitacionRepository.findById(dto.idHabitacion())
                .orElseThrow(() -> new BadRequestException("Habitación no encontrada con id: " + dto.idHabitacion()));
        var tipoAlq = tipoAlquilerRepository.findById(dto.idTipoAlquiler())
                .orElseThrow(() -> new BadRequestException("Tipo de alquiler no encontrado con id: " + dto.idTipoAlquiler()));
        var cliente = clienteRepository.findById(dto.idCliente())
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado con id: " + dto.idCliente()));
        var usuario = usuarioRepository.findByNumDocumento(dniUsuario)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado: " + dniUsuario));
        var tarifa = tarifaRepository.findByTipoHabitacionIdAndTipoAlquilerId(
                habitacion.getTipoHabitacion().getId(), tipoAlq.getId())
                .orElseThrow(() -> new BadRequestException("No hay tarifa para esta combinación de habitación y tipo de alquiler"));

        if (habitacion.getEstado() != EstadoHabitacion.DISPONIBLE) {
            throw new BadRequestException("La habitación no está disponible para alquiler.");
        }

        LocalDateTime ahora = LocalDateTime.now();
        long totalUnidades = (long) dto.cantTiempo() * tipoAlq.getMultiplicador();
        LocalDateTime fechaPrevista = "HORA".equalsIgnoreCase(tipoAlq.getUnidad())
                ? ahora.plusHours(totalUnidades)
                : ahora.plusDays(totalUnidades);

        BigDecimal precioFijado = tarifa.getPrecio();
        BigDecimal subTotal = precioFijado.multiply(BigDecimal.valueOf(dto.cantTiempo()));
        BigDecimal adelanto = (dto.adelanto() != null) ? dto.adelanto() : BigDecimal.ZERO;
        BigDecimal pendiente = subTotal.subtract(adelanto);

        Alquiler alquiler = new Alquiler();
        alquiler.setFechaIngreso(ahora);
        alquiler.setCliente(cliente);
        alquiler.setHabitacion(habitacion);
        alquiler.setTarifa(tarifa);
        alquiler.setUsuario(usuario);
        alquiler.setPrecioFijado(precioFijado);
        alquiler.setCantTiempo(dto.cantTiempo());
        alquiler.setPagoPendiente(pendiente);
        alquiler.setEstado(EstadoAlquiler.ACTIVO);
        alquiler.setFechaPrevista(fechaPrevista);
        alquiler.setEmpresa(cliente.getEmpresa());

        // Asignar huéspedes: si viene lista, usarla; si no, solo el representante
        if (dto.idHuespedes() != null && !dto.idHuespedes().isEmpty()) {
            List<com.proyecto.hotel.model.entities.Cliente> huespedesEntities =
                new java.util.ArrayList<>(clienteRepository.findAllById(dto.idHuespedes()));
            if (huespedesEntities.stream().noneMatch(h -> h.getId().equals(cliente.getId()))) {
                huespedesEntities.add(0, cliente);
            }
            alquiler.setHuespedes(huespedesEntities);
        } else {
            alquiler.setHuespedes(new java.util.ArrayList<>(List.of(cliente)));
        }

        var guardado = alquilerRepository.save(alquiler);

        habitacion.setEstado(EstadoHabitacion.OCUPADA);
        habitacionRepository.save(habitacion);

        // 6. Registro en Caja si hubo pago (si no se indicó método, se asume EFECTIVO)
        if (adelanto.compareTo(BigDecimal.ZERO) > 0) {
            MetodoPago metodo = (dto.metodoPago() != null) ? dto.metodoPago() : MetodoPago.EFECTIVO;
            registrarMovimientoCaja(adelanto, metodo, TipoMovimiento.INGRESO, guardado, usuario, "Check-in");
        }

        return mapearResponse(guardado);
    }

    @Override
    @Transactional
    public AlquilerResponseDTO registrarCheckOut(Long idAlquiler, String dniUsuario, MetodoPago metodoPago) {
        Alquiler alquiler = alquilerRepository.findById(idAlquiler)
                .orElseThrow(() -> new BadRequestException("Alquiler no encontrado con id: " + idAlquiler));
        Usuario usuario = usuarioRepository.findByNumDocumento(dniUsuario)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado: " + dniUsuario));

        alquiler.setFechaSalida(LocalDateTime.now());
        alquiler.setEstado(EstadoAlquiler.FINALIZADO);

        boolean esEmpresa = alquiler.getEmpresa() != null;

        // Para clientes normales: marcar todos los cargos pendientes como PAGADO al hacer checkout.
        // Para empresa: dejar los consumos en PENDIENTE para que el admin les asigne precio y cobre.
        if (!esEmpresa) {
            List<CuentaAlquiler> cuentasPendientes = cuentaAlquilerRepository
                    .findByAlquilerIdAndEstado(idAlquiler, EstadoCuenta.PENDIENTE);
            for (CuentaAlquiler cuenta : cuentasPendientes) {
                cuenta.setEstado(EstadoCuenta.PAGADO);
            }
            cuentaAlquilerRepository.saveAll(cuentasPendientes);
        }

        if (esEmpresa) {
            // Para empresa: siempre crear movimiento PENDIENTE (aunque pagoPendiente sea 0),
            // así el admin siempre lo ve en Cuentas Empresa para asignar precios a consumos.
            BigDecimal monto = alquiler.getPagoPendiente().compareTo(BigDecimal.ZERO) > 0
                    ? alquiler.getPagoPendiente()
                    : BigDecimal.ZERO;
            registrarMovimientoCaja(monto, null, TipoMovimiento.PENDIENTE, alquiler, usuario, "Liquidación Check-out");
            alquiler.setPagoPendiente(BigDecimal.ZERO);
        } else if (alquiler.getPagoPendiente().compareTo(BigDecimal.ZERO) > 0) {
            registrarMovimientoCaja(alquiler.getPagoPendiente(), metodoPago, TipoMovimiento.INGRESO, alquiler, usuario, "Liquidación Check-out");
            alquiler.setPagoPendiente(BigDecimal.ZERO);
        }

        alquiler.getHabitacion().setEstado(EstadoHabitacion.LIMPIEZA);
        habitacionRepository.save(alquiler.getHabitacion());
        var guardado = alquilerRepository.save(alquiler);
        return mapearResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponseDTO> listarAlquileresActivos() {
        List<Alquiler> lista = alquilerRepository.findByEstado(EstadoAlquiler.ACTIVO);
        Map<Long, BigDecimal> sumMap = buildSumMap(lista);
        return lista.stream()
                .map(a -> mapearResponse(a, sumMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponseDTO> listarHistorial() {
        List<Alquiler> lista = alquilerRepository.findByEstado(EstadoAlquiler.FINALIZADO);
        Map<Long, BigDecimal> sumMap = buildSumMap(lista);
        return lista.stream()
                .map(a -> mapearResponse(a, sumMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AlquilerResponseDTO obtenerAlquilerPorId(Long id) {
        Alquiler alquiler = alquilerRepository.findAlquilerById(id)
                .orElseThrow(() -> new BadRequestException("Alquiler no encontrado con id: " + id));
        return mapearResponse(alquiler);
    }

    @Override
    @Transactional
    public AlquilerResponseDTO actualizarMontos(Long id, BigDecimal subTotal, BigDecimal pagoPendiente) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Alquiler no encontrado con id: " + id));

        if (alquiler.getEstado() != EstadoAlquiler.ACTIVO && alquiler.getEstado() != EstadoAlquiler.FINALIZADO) {
            throw new BadRequestException("Solo se pueden modificar alquileres activos o finalizados.");
        }

        if (subTotal.compareTo(BigDecimal.ZERO) < 0 || pagoPendiente.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Los montos no pueden ser negativos");
        }

        if (alquiler.getCantTiempo() == null || alquiler.getCantTiempo() <= 0) {
            throw new BadRequestException("No se puede recalcular tarifa: cantidad de tiempo inválida");
        }

        BigDecimal precioFijado = subTotal.divide(BigDecimal.valueOf(alquiler.getCantTiempo()), 2, java.math.RoundingMode.HALF_UP);
        alquiler.setPrecioFijado(precioFijado);
        alquiler.setPagoPendiente(pagoPendiente);

        return mapearResponse(alquilerRepository.save(alquiler));
    }

    private void registrarMovimientoCaja(BigDecimal monto, MetodoPago metodo, TipoMovimiento tipo, Alquiler alq, Usuario usu, String concepto) {
        MovimientoCaja mov = new MovimientoCaja();
        mov.setTipo(tipo);
        mov.setMonto(monto);
        mov.setMetodoPago(metodo);
        mov.setConcepto(concepto + " - Hab: " + alq.getHabitacion().getNumero());
        mov.setAlquiler(alq);
        mov.setUsuario(usu);
        cajaRepository.save(mov);
    }

    private Map<Long, BigDecimal> buildSumMap(List<Alquiler> lista) {
        List<Long> ids = lista.stream().map(Alquiler::getId).collect(Collectors.toList());
        if (ids.isEmpty()) return new HashMap<>();
        Map<Long, BigDecimal> map = new HashMap<>();
        for (Object[] row : cajaRepository.sumNetByAlquilerIds(ids)) {
            map.put((Long) row[0], (BigDecimal) row[1]);
        }
        return map;
    }

    private AlquilerResponseDTO mapearResponse(Alquiler a) {
        return mapearResponse(a, null);
    }

    private AlquilerResponseDTO mapearResponse(Alquiler a, Map<Long, BigDecimal> sumMap) {
    BigDecimal subTotal = a.getPrecioFijado().multiply(BigDecimal.valueOf(a.getCantTiempo()));
    BigDecimal totalPagadoCaja = (sumMap != null)
        ? sumMap.getOrDefault(a.getId(), BigDecimal.ZERO)
        : cajaRepository.sumNetByAlquilerId(a.getId());
    String empresaNombre = (a.getEmpresa() != null)
        ? a.getEmpresa().getNombre()
        : (a.getCliente() != null && a.getCliente().getEmpresa() != null)
            ? a.getCliente().getEmpresa().getNombre()
            : "—";
    
    // Si la fecha de ingreso es null (porque aún no se refrescó de la DB), usamos la actual
    LocalDateTime fechaInicio = (a.getFechaIngreso() != null) ? a.getFechaIngreso() : LocalDateTime.now();

    String tipoAlquilerNombre = (a.getTarifa() != null && a.getTarifa().getTipoAlquiler() != null)
        ? a.getTarifa().getTipoAlquiler().getNombre() : "—";

    List<String> huespedesNombres = (a.getHuespedes() != null)
        ? a.getHuespedes().stream().map(com.proyecto.hotel.model.entities.Cliente::getNombre).collect(Collectors.toList())
        : List.of();

    return new AlquilerResponseDTO(
        a.getId(),
        a.getHabitacion().getNumero(),
        a.getCliente().getNombre(),
        empresaNombre,
        tipoAlquilerNombre,
        totalPagadoCaja,
        subTotal,
        a.getPagoPendiente(),
        fechaInicio,
        a.getFechaPrevista(),
        a.getFechaSalida(),
        a.getEstado().name(),
        a.getHabitacion().getEstado().name(),
        huespedesNombres
    );
}

    @Override
    @Transactional
    public AlquilerResponseDTO agregarHuesped(Long idAlquiler, Long idCliente) {
        Alquiler alquiler = alquilerRepository.findAlquilerWithHuespedesById(idAlquiler)
                .orElseThrow(() -> new BadRequestException("Alquiler no encontrado con id: " + idAlquiler));
        if (alquiler.getEstado() != EstadoAlquiler.ACTIVO) {
            throw new BadRequestException("Solo se pueden modificar alquileres activos.");
        }
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado con id: " + idCliente));
        boolean yaEsta = alquiler.getHuespedes().stream().anyMatch(h -> h.getId().equals(idCliente));
        if (!yaEsta) {
            alquiler.getHuespedes().add(cliente);
            alquilerRepository.save(alquiler);
        }
        return mapearResponse(alquiler);
    }

    @Override
    @Transactional
    public AlquilerResponseDTO quitarHuesped(Long idAlquiler, Long idCliente) {
        Alquiler alquiler = alquilerRepository.findAlquilerWithHuespedesById(idAlquiler)
                .orElseThrow(() -> new BadRequestException("Alquiler no encontrado con id: " + idAlquiler));
        if (alquiler.getEstado() != EstadoAlquiler.ACTIVO) {
            throw new BadRequestException("Solo se pueden modificar alquileres activos.");
        }
        if (alquiler.getCliente().getId().equals(idCliente)) {
            throw new BadRequestException("No se puede quitar al representante principal del alquiler.");
        }
        alquiler.getHuespedes().removeIf(h -> h.getId().equals(idCliente));
        alquilerRepository.save(alquiler);
        return mapearResponse(alquiler);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponseDTO> reporteMensual(Long idHabitacion, int mes, int anio) {
        List<Alquiler> lista = alquilerRepository.findByHabitacionIdAndMesAnio(idHabitacion, mes, anio);
        Map<Long, BigDecimal> sumMap = buildSumMap(lista);
        return lista.stream()
                .map(a -> mapearResponse(a, sumMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> previsualizarEliminacionHistorial(java.time.LocalDate desde, java.time.LocalDate hasta) {
        long cantidad;
        String periodoDesc;

        if (desde != null && hasta != null) {
            if (hasta.isBefore(desde)) throw new BadRequestException("La fecha 'hasta' no puede ser anterior a 'desde'");
            LocalDateTime inicio = desde.atStartOfDay();
            LocalDateTime fin = hasta.atTime(java.time.LocalTime.MAX);
            cantidad = alquilerRepository.countByEstadoAndFechaIngresoBetween(EstadoAlquiler.FINALIZADO, inicio, fin);
            periodoDesc = desde + " — " + hasta;
        } else {
            cantidad = alquilerRepository.countByEstado(EstadoAlquiler.FINALIZADO);
            periodoDesc = "TODO el historial";
        }

        return Map.of(
            "cantidad", cantidad,
            "periodo", periodoDesc
        );
    }

    @Override
    @Transactional
    public int eliminarHistorial(java.time.LocalDate desde, java.time.LocalDate hasta, String adminDni) {
        List<Long> ids;
        String periodoDesc;

        if (desde != null && hasta != null) {
            if (hasta.isBefore(desde)) throw new BadRequestException("La fecha 'hasta' no puede ser anterior a 'desde'");
            LocalDateTime inicio = desde.atStartOfDay();
            LocalDateTime fin = hasta.atTime(java.time.LocalTime.MAX);
            ids = alquilerRepository.findIdsByEstadoAndFechaIngresoBetween(EstadoAlquiler.FINALIZADO, inicio, fin);
            periodoDesc = desde + " — " + hasta;
        } else {
            ids = alquilerRepository.findIdsByEstado(EstadoAlquiler.FINALIZADO);
            periodoDesc = "TODO";
        }

        if (ids.isEmpty()) return 0;

        // desvincular movimientos de caja
        cajaRepository.desvincularAlquileresPorIds(ids);

        // eliminar alquileres (cuenta_alquiler y alquiler_cliente cascadean automáticamente)
        int deleted;
        if (desde != null && hasta != null) {
            LocalDateTime inicio = desde.atStartOfDay();
            LocalDateTime fin = hasta.atTime(java.time.LocalTime.MAX);
            deleted = alquilerRepository.deleteByEstadoAndFechaIngresoBetween(EstadoAlquiler.FINALIZADO, inicio, fin);
        } else {
            deleted = alquilerRepository.deleteByEstado(EstadoAlquiler.FINALIZADO);
        }

        return deleted;
    }
}
