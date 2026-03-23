package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.TarifaDTO;
import com.proyecto.hotel.model.entities.Tarifa;
import com.proyecto.hotel.model.mapper.TarifaMapper;
import com.proyecto.hotel.model.repository.TarifaRepository;
import com.proyecto.hotel.service.TarifaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TarifaServiceImpl implements TarifaService {

    private final TarifaRepository tarifaRepository;
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
        Tarifa tarifa = tarifaMapper.toEntity(tarifaDTO);
        tarifa = tarifaRepository.save(tarifa);
        return tarifaMapper.toDTO(tarifaRepository.findById(tarifa.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public TarifaDTO actualizarTarifa(Long id, TarifaDTO tarifaDTO) {
        Tarifa tarifa = tarifaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tarifa no encontrada con id: " + id));
        tarifaMapper.updateEntityFromDTO(tarifaDTO, tarifa);
        tarifa = tarifaRepository.save(tarifa);
        return tarifaMapper.toDTO(tarifaRepository.findById(tarifa.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public void eliminarTarifa(Long id) {
        tarifaRepository.deleteById(id);
    }
}