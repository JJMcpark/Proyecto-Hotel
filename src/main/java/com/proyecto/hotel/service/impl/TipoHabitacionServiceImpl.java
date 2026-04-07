package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.TipoHabitacionDTO;
import com.proyecto.hotel.model.entities.TipoHabitacion;
import com.proyecto.hotel.model.mapper.TipoHabitacionMapper;
import com.proyecto.hotel.model.repository.HabitacionRepository;
import com.proyecto.hotel.model.repository.TarifaRepository;
import com.proyecto.hotel.model.repository.TipoHabitacionRepository;
import com.proyecto.hotel.service.TipoHabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoHabitacionServiceImpl implements TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HabitacionRepository habitacionRepository;
    private final TarifaRepository tarifaRepository;
    private final TipoHabitacionMapper tipoHabitacionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TipoHabitacionDTO> obtenerTodosLosTiposHabitacion() {
        return tipoHabitacionRepository.findAll().stream()
                .map(tipoHabitacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TipoHabitacionDTO obtenerTipoHabitacionPorId(Long id) {
        TipoHabitacion tipoHabitacion = tipoHabitacionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tipo de habitación no encontrado con id: " + id));
        return tipoHabitacionMapper.toDTO(tipoHabitacion);
    }

    @Override
    @Transactional
    public TipoHabitacionDTO crearTipoHabitacion(TipoHabitacionDTO tipoHabitacionDTO) {
        TipoHabitacion tipoHabitacion = tipoHabitacionMapper.toEntity(tipoHabitacionDTO);
        tipoHabitacion = tipoHabitacionRepository.save(tipoHabitacion);
        return tipoHabitacionMapper.toDTO(tipoHabitacion);
    }

    @Override
    @Transactional
    public TipoHabitacionDTO actualizarTipoHabitacion(Long id, TipoHabitacionDTO tipoHabitacionDTO) {
        TipoHabitacion tipoHabitacion = tipoHabitacionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tipo de habitación no encontrado con id: " + id));
        tipoHabitacionMapper.updateEntityFromDTO(tipoHabitacionDTO, tipoHabitacion);
        tipoHabitacion = tipoHabitacionRepository.save(tipoHabitacion);
        return tipoHabitacionMapper.toDTO(tipoHabitacion);
    }

    @Override
    @Transactional
    public void eliminarTipoHabitacion(Long id) {
        if (!tipoHabitacionRepository.existsById(id)) {
            throw new BadRequestException("Tipo de habitación no encontrado con id: " + id);
        }
        if (habitacionRepository.existsByTipoHabitacionId(id)) {
            throw new BadRequestException("No se puede eliminar: hay habitaciones que usan este tipo de habitación");
        }
        tarifaRepository.deleteByTipoHabitacionId(id);
        tipoHabitacionRepository.deleteById(id);
    }
}