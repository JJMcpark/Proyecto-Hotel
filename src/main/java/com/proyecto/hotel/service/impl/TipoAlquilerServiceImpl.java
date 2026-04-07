package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.TipoAlquilerDTO;
import com.proyecto.hotel.model.entities.TipoAlquiler;
import com.proyecto.hotel.model.mapper.TipoAlquilerMapper;
import com.proyecto.hotel.model.repository.TarifaRepository;
import com.proyecto.hotel.model.repository.TipoAlquilerRepository;
import com.proyecto.hotel.service.TipoAlquilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoAlquilerServiceImpl implements TipoAlquilerService {

    private final TipoAlquilerRepository tipoAlquilerRepository;
    private final TarifaRepository tarifaRepository;
    private final TipoAlquilerMapper tipoAlquilerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TipoAlquilerDTO> obtenerTodosLosTiposAlquiler() {
        return tipoAlquilerRepository.findAll().stream()
                .map(tipoAlquilerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TipoAlquilerDTO obtenerTipoAlquilerPorId(Long id) {
        TipoAlquiler tipoAlquiler = tipoAlquilerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tipo de alquiler no encontrado con id: " + id));
        return tipoAlquilerMapper.toDTO(tipoAlquiler);
    }

    @Override
    @Transactional
    public TipoAlquilerDTO crearTipoAlquiler(TipoAlquilerDTO tipoAlquilerDTO) {
        TipoAlquiler tipoAlquiler = tipoAlquilerMapper.toEntity(tipoAlquilerDTO);
        tipoAlquiler = tipoAlquilerRepository.save(tipoAlquiler);
        return tipoAlquilerMapper.toDTO(tipoAlquiler);
    }

    @Override
    @Transactional
    public TipoAlquilerDTO actualizarTipoAlquiler(Long id, TipoAlquilerDTO tipoAlquilerDTO) {
        TipoAlquiler tipoAlquiler = tipoAlquilerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tipo de alquiler no encontrado con id: " + id));
        tipoAlquilerMapper.updateEntityFromDTO(tipoAlquilerDTO, tipoAlquiler);
        tipoAlquiler = tipoAlquilerRepository.save(tipoAlquiler);
        return tipoAlquilerMapper.toDTO(tipoAlquiler);
    }

    @Override
    @Transactional
    public void eliminarTipoAlquiler(Long id) {
        if (!tipoAlquilerRepository.existsById(id)) {
            throw new BadRequestException("Tipo de alquiler no encontrado con id: " + id);
        }
        tarifaRepository.deleteByTipoAlquilerId(id);
        tipoAlquilerRepository.deleteById(id);
    }
}