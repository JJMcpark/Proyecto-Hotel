package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.CuentaAlquilerDTO;
import com.proyecto.hotel.model.entities.Alquiler;
import com.proyecto.hotel.model.entities.CuentaAlquiler;
import com.proyecto.hotel.model.entities.MovimientoCaja;
import com.proyecto.hotel.model.enums.EstadoAlquiler;
import com.proyecto.hotel.model.enums.EstadoCuenta;
import com.proyecto.hotel.model.enums.MetodoPago;
import com.proyecto.hotel.model.enums.TipoMovimiento;
import com.proyecto.hotel.model.mapper.CuentaAlquilerMapper;
import com.proyecto.hotel.model.repository.AlquilerRepository;
import com.proyecto.hotel.model.repository.CuentaAlquilerRepository;
import com.proyecto.hotel.model.repository.MovimientoCajaRepository;
import com.proyecto.hotel.model.repository.UsuarioRepository;
import com.proyecto.hotel.service.CuentaAlquilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuentaAlquilerServiceImpl implements CuentaAlquilerService {

    private final CuentaAlquilerRepository cuentaAlquilerRepository;
    private final AlquilerRepository alquilerRepository;
    private final CuentaAlquilerMapper cuentaAlquilerMapper;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;

    @Override
    public List<CuentaAlquilerDTO> obtenerCuentasPorAlquiler(Long alquilerId) {
        if (!alquilerRepository.existsById(alquilerId)) {
            throw new BadRequestException("Alquiler no encontrado con id: " + alquilerId);
        }
        return cuentaAlquilerRepository.findByAlquilerId(alquilerId).stream()
                .map(cuentaAlquilerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CuentaAlquilerDTO agregarCargo(Long alquilerId, CuentaAlquilerDTO dto) {
        Alquiler alquiler = alquilerRepository.findById(alquilerId)
                .orElseThrow(() -> new BadRequestException("Alquiler no encontrado con id: " + alquilerId));

        if (alquiler.getEstado() != EstadoAlquiler.ACTIVO) {
            throw new BadRequestException("No se pueden agregar cargos a un alquiler que no está activo");
        }

        CuentaAlquiler cuenta = cuentaAlquilerMapper.toEntity(dto);
        cuenta.setAlquiler(alquiler);
        cuenta.setSubTotal(dto.getPrecioUnit().multiply(BigDecimal.valueOf(dto.getCantidad())));

        // Sumar el cargo al pago pendiente del alquiler
        alquiler.setPagoPendiente(alquiler.getPagoPendiente().add(cuenta.getSubTotal()));
        alquilerRepository.save(alquiler);

        return cuentaAlquilerMapper.toDTO(cuentaAlquilerRepository.save(cuenta));
    }

    @Override
    @Transactional
    public CuentaAlquilerDTO actualizarCargo(Long id, CuentaAlquilerDTO dto, String dniUsuario, MetodoPago metodoPago) {
        CuentaAlquiler cuenta = cuentaAlquilerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Cargo no encontrado con id: " + id));

        Alquiler alquiler = cuenta.getAlquiler();
        EstadoCuenta estadoAnterior = cuenta.getEstado();
        BigDecimal subTotalAnterior = cuenta.getSubTotal();

        cuentaAlquilerMapper.updateEntityFromDTO(dto, cuenta);
        cuenta.setSubTotal(cuenta.getPrecioUnit().multiply(BigDecimal.valueOf(cuenta.getCantidad())));

        BigDecimal contribucionAnterior = EstadoCuenta.PAGADO.equals(estadoAnterior) ? BigDecimal.ZERO : subTotalAnterior;
        BigDecimal contribucionNueva = EstadoCuenta.PAGADO.equals(cuenta.getEstado()) ? BigDecimal.ZERO : cuenta.getSubTotal();
        BigDecimal diferencia = contribucionNueva.subtract(contribucionAnterior);
        alquiler.setPagoPendiente(alquiler.getPagoPendiente().add(diferencia));
        alquilerRepository.save(alquiler);

        if (!EstadoCuenta.PAGADO.equals(estadoAnterior) && EstadoCuenta.PAGADO.equals(cuenta.getEstado())) {
            var usuario = usuarioRepository.findByNumDocumento(dniUsuario)
                    .orElseThrow(() -> new BadRequestException("Usuario no encontrado: " + dniUsuario));
            MovimientoCaja movimiento = new MovimientoCaja();
            movimiento.setTipo(TipoMovimiento.INGRESO);
            movimiento.setMonto(cuenta.getSubTotal());
            movimiento.setMetodoPago(metodoPago != null ? metodoPago : MetodoPago.EFECTIVO);
            movimiento.setConcepto("Pago consumo - Hab: " + alquiler.getHabitacion().getNumero() + " - " + cuenta.getDescripcion());
            movimiento.setAlquiler(alquiler);
            movimiento.setUsuario(usuario);
            movimientoCajaRepository.save(movimiento);
        }

        return cuentaAlquilerMapper.toDTO(cuentaAlquilerRepository.save(cuenta));
    }

    @Override
    @Transactional
    public void eliminarCargo(Long id) {
        CuentaAlquiler cuenta = cuentaAlquilerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Cargo no encontrado con id: " + id));

        // Solo restar del pago pendiente cuando el cargo aun estaba pendiente.
        // Si ya estaba pagado, su impacto en pagoPendiente ya fue descontado al marcar PAGADO.
        Alquiler alquiler = cuenta.getAlquiler();
        if (!EstadoCuenta.PAGADO.equals(cuenta.getEstado())) {
            alquiler.setPagoPendiente(alquiler.getPagoPendiente().subtract(cuenta.getSubTotal()));
        }
        alquilerRepository.save(alquiler);

        cuentaAlquilerRepository.deleteById(id);
    }
}
