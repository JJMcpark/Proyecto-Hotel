package com.proyecto.hotel.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.hotel.controller.request.CheckInRequestDTO;
import com.proyecto.hotel.controller.response.AlquilerResponseDTO;
import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.entities.Alquiler;
import com.proyecto.hotel.model.entities.MovimientoCaja;
import com.proyecto.hotel.model.entities.Usuario;
import com.proyecto.hotel.model.enums.EstadoAlquiler;
import com.proyecto.hotel.model.enums.EstadoHabitacion;
import com.proyecto.hotel.model.enums.MetodoPago;
import com.proyecto.hotel.model.enums.TipoMovimiento;
import com.proyecto.hotel.model.repository.AlquilerRepository;

import java.util.List;
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
        LocalDateTime fechaPrevista = tipoAlq.getNombre().equalsIgnoreCase("POR HORA") 
                ? ahora.plusHours(dto.cantTiempo()) 
                : ahora.plusDays(dto.cantTiempo());

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
        
        var guardado = alquilerRepository.save(alquiler);

        habitacion.setEstado(EstadoHabitacion.OCUPADA);
        habitacionRepository.save(habitacion);

        // 6. Registro en Caja si hubo pago
        if (adelanto.compareTo(BigDecimal.ZERO) > 0) {
            registrarMovimientoCaja(adelanto, dto.metodoPago(), guardado, usuario, "Check-in");
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

        // Marcar todos los cargos pendientes como PAGADO
        List<CuentaAlquiler> cuentasPendientes = cuentaAlquilerRepository
                .findByAlquilerIdAndEstado(idAlquiler, EstadoCuenta.PENDIENTE);
        for (CuentaAlquiler cuenta : cuentasPendientes) {
            cuenta.setEstado(EstadoCuenta.PAGADO);
        }
        cuentaAlquilerRepository.saveAll(cuentasPendientes);

        if (alquiler.getPagoPendiente().compareTo(BigDecimal.ZERO) > 0) {
            registrarMovimientoCaja(alquiler.getPagoPendiente(), metodoPago, alquiler, usuario, "Liquidación Check-out");
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
        return alquilerRepository.findByEstado(EstadoAlquiler.ACTIVO).stream()
                .map(this::mapearResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponseDTO> listarHistorial() {
        return alquilerRepository.findByEstado(EstadoAlquiler.FINALIZADO).stream()
                .map(this::mapearResponse)
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

    private void registrarMovimientoCaja(BigDecimal monto, MetodoPago metodo, Alquiler alq, Usuario usu, String concepto) {
        MovimientoCaja mov = new MovimientoCaja();
        mov.setTipo(TipoMovimiento.INGRESO);
        mov.setMonto(monto);
        mov.setMetodoPago(metodo);
        mov.setConcepto(concepto + " - Hab: " + alq.getHabitacion().getNumero());
        mov.setAlquiler(alq);
        mov.setUsuario(usu);
        cajaRepository.save(mov);
    }

    private AlquilerResponseDTO mapearResponse(Alquiler a) {
    BigDecimal subTotal = a.getPrecioFijado().multiply(BigDecimal.valueOf(a.getCantTiempo()));
    BigDecimal totalPagadoCaja = cajaRepository.sumIngresosByAlquilerId(a.getId());
    String empresaNombre = (a.getCliente() != null && a.getCliente().getEmpresa() != null)
        ? a.getCliente().getEmpresa().getNombre()
        : (a.getEmpresa() != null ? a.getEmpresa().getNombre() : "—");
    
    // Si la fecha de ingreso es null (porque aún no se refrescó de la DB), usamos la actual
    LocalDateTime fechaInicio = (a.getFechaIngreso() != null) ? a.getFechaIngreso() : LocalDateTime.now();

    return new AlquilerResponseDTO(
        a.getId(),
        a.getHabitacion().getNumero(),
        a.getCliente().getNombre(),
        empresaNombre,
        totalPagadoCaja,
        subTotal,
        a.getPagoPendiente(),
        fechaInicio,
        a.getFechaPrevista(),
        a.getEstado().name(),              // .name() convierte el Enum Alquiler a String
        a.getHabitacion().getEstado().name() // .name() convierte el Enum Habitación a String
    );
}
}