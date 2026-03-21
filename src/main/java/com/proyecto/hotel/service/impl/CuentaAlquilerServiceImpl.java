package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.CuentaAlquilerDTO;
import com.proyecto.hotel.model.entities.Alquiler;
import com.proyecto.hotel.model.entities.CuentaAlquiler;
import com.proyecto.hotel.model.mapper.CuentaAlquilerMapper;
import com.proyecto.hotel.model.repository.AlquilerRepository;
import com.proyecto.hotel.model.repository.CuentaAlquilerRepository;
import com.proyecto.hotel.service.CuentaAlquilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public CuentaAlquilerDTO agregarCargo(Long alquilerId, CuentaAlquilerDTO dto) {
        Alquiler alquiler = alquilerRepository.findById(alquilerId)
                .orElseThrow(() -> new BadRequestException("Alquiler no encontrado con id: " + alquilerId));

        CuentaAlquiler cuenta = cuentaAlquilerMapper.toEntity(dto);
        cuenta.setAlquiler(alquiler);
        cuenta.setSubTotal(dto.getPrecioUnit().multiply(BigDecimal.valueOf(dto.getCantidad())));

        return cuentaAlquilerMapper.toDTO(cuentaAlquilerRepository.save(cuenta));
    }

    @Override
    public CuentaAlquilerDTO actualizarCargo(Long id, CuentaAlquilerDTO dto) {
        CuentaAlquiler cuenta = cuentaAlquilerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Cargo no encontrado con id: " + id));
        cuentaAlquilerMapper.updateEntityFromDTO(dto, cuenta);
        cuenta.setSubTotal(cuenta.getPrecioUnit().multiply(BigDecimal.valueOf(cuenta.getCantidad())));
        return cuentaAlquilerMapper.toDTO(cuentaAlquilerRepository.save(cuenta));
    }

    @Override
    public void eliminarCargo(Long id) {
        if (!cuentaAlquilerRepository.existsById(id)) {
            throw new BadRequestException("Cargo no encontrado con id: " + id);
        }
        cuentaAlquilerRepository.deleteById(id);
    }
}
