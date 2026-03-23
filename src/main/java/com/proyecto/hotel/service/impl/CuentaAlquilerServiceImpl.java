package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.CuentaAlquilerDTO;
import com.proyecto.hotel.model.entities.Alquiler;
import com.proyecto.hotel.model.entities.CuentaAlquiler;
import com.proyecto.hotel.model.enums.EstadoAlquiler;
import com.proyecto.hotel.model.mapper.CuentaAlquilerMapper;
import com.proyecto.hotel.model.repository.AlquilerRepository;
import com.proyecto.hotel.model.repository.CuentaAlquilerRepository;
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
    public CuentaAlquilerDTO actualizarCargo(Long id, CuentaAlquilerDTO dto) {
        CuentaAlquiler cuenta = cuentaAlquilerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Cargo no encontrado con id: " + id));

        Alquiler alquiler = cuenta.getAlquiler();
        BigDecimal subTotalAnterior = cuenta.getSubTotal();

        cuentaAlquilerMapper.updateEntityFromDTO(dto, cuenta);
        cuenta.setSubTotal(cuenta.getPrecioUnit().multiply(BigDecimal.valueOf(cuenta.getCantidad())));

        // Ajustar pagoPendiente: restar el anterior, sumar el nuevo
        BigDecimal diferencia = cuenta.getSubTotal().subtract(subTotalAnterior);
        alquiler.setPagoPendiente(alquiler.getPagoPendiente().add(diferencia));
        alquilerRepository.save(alquiler);

        return cuentaAlquilerMapper.toDTO(cuentaAlquilerRepository.save(cuenta));
    }

    @Override
    @Transactional
    public void eliminarCargo(Long id) {
        CuentaAlquiler cuenta = cuentaAlquilerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Cargo no encontrado con id: " + id));

        // Restar del pago pendiente del alquiler
        Alquiler alquiler = cuenta.getAlquiler();
        alquiler.setPagoPendiente(alquiler.getPagoPendiente().subtract(cuenta.getSubTotal()));
        alquilerRepository.save(alquiler);

        cuentaAlquilerRepository.deleteById(id);
    }
}
