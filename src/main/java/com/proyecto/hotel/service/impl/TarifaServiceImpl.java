package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.TarifaDTO;
import com.proyecto.hotel.model.entities.Tarifa;
import com.proyecto.hotel.model.entities.TipoHabitacion;
import com.proyecto.hotel.model.entities.TipoAlquiler;
import com.proyecto.hotel.model.mapper.TarifaMapper;
import com.proyecto.hotel.model.repository.TarifaRepository;
import com.proyecto.hotel.model.repository.TipoHabitacionRepository;
import com.proyecto.hotel.model.repository.TipoAlquilerRepository;
import com.proyecto.hotel.service.TarifaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TarifaServiceImpl implements TarifaService {

    private final TarifaRepository tarifaRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final TipoAlquilerRepository tipoAlquilerRepository;
    private final TarifaMapper tarifaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TarifaDTO> obtenerTodasLasTarifas() {
        return tarifaRepository.findAll().stream()
                .map(tarifaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TarifaDTO obtenerTarifaPorId(Long id) {
        Tarifa tarifa = tarifaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tarifa no encontrada con id: " + id));
        return tarifaMapper.toDTO(tarifa);
    }

    @Override
    @Transactional
    public TarifaDTO crearTarifa(TarifaDTO tarifaDTO) {
        TipoHabitacion tipoHab = tipoHabitacionRepository.findById(tarifaDTO.getTipoHabitacionId())
                .orElseThrow(() -> new BadRequestException("Tipo de habitación no encontrado"));
        TipoAlquiler tipoAlq = tipoAlquilerRepository.findById(tarifaDTO.getTipoAlquilerId())
                .orElseThrow(() -> new BadRequestException("Tipo de alquiler no encontrado"));
        
        Tarifa tarifa = Tarifa.builder()
                .precio(tarifaDTO.getPrecio())
                .tipoHabitacion(tipoHab)
                .tipoAlquiler(tipoAlq)
                .build();
        
        tarifa = tarifaRepository.save(tarifa);
        return tarifaMapper.toDTO(tarifaRepository.findById(tarifa.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public TarifaDTO actualizarTarifa(Long id, TarifaDTO tarifaDTO) {
        Tarifa tarifa = tarifaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tarifa no encontrada con id: " + id));
        
        if (tarifaDTO.getTipoHabitacionId() != null) {
            TipoHabitacion tipoHab = tipoHabitacionRepository.findById(tarifaDTO.getTipoHabitacionId())
                    .orElseThrow(() -> new BadRequestException("Tipo de habitación no encontrado"));
            tarifa.setTipoHabitacion(tipoHab);
        }
        
        if (tarifaDTO.getTipoAlquilerId() != null) {
            TipoAlquiler tipoAlq = tipoAlquilerRepository.findById(tarifaDTO.getTipoAlquilerId())
                    .orElseThrow(() -> new BadRequestException("Tipo de alquiler no encontrado"));
            tarifa.setTipoAlquiler(tipoAlq);
        }
        
        if (tarifaDTO.getPrecio() != null) {
            tarifa.setPrecio(tarifaDTO.getPrecio());
        }
        
        tarifa = tarifaRepository.save(tarifa);
        return tarifaMapper.toDTO(tarifaRepository.findById(tarifa.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public List<TarifaDTO> incrementarTarifasPorcentaje(BigDecimal porcentaje) {
        if (porcentaje.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El porcentaje debe ser mayor a 0");
        }

        BigDecimal factor = BigDecimal.ONE.add(porcentaje.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        List<Tarifa> tarifas = tarifaRepository.findAll();

        tarifas.forEach(t -> t.setPrecio(t.getPrecio().multiply(factor).setScale(2, RoundingMode.HALF_UP)));
        return tarifaRepository.saveAll(tarifas).stream().map(tarifaMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarTarifa(Long id) {
        tarifaRepository.deleteById(id);
    }
}