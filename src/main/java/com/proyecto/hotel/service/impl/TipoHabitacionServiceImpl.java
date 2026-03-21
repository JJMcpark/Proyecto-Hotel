package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.TipoHabitacionDTO;
import com.proyecto.hotel.model.entities.TipoHabitacion;
import com.proyecto.hotel.model.mapper.TipoHabitacionMapper;
import com.proyecto.hotel.model.repository.TipoHabitacionRepository;
import com.proyecto.hotel.service.TipoHabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoHabitacionServiceImpl implements TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final TipoHabitacionMapper tipoHabitacionMapper;

    @Override
    public List<TipoHabitacionDTO> obtenerTodosLosTiposHabitacion() {
        return tipoHabitacionRepository.findAll().stream()
                .map(tipoHabitacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TipoHabitacionDTO obtenerTipoHabitacionPorId(Long id) {
        TipoHabitacion tipoHabitacion = tipoHabitacionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tipo de habitación no encontrado con id: " + id));
        return tipoHabitacionMapper.toDTO(tipoHabitacion);
    }

    @Override
    public TipoHabitacionDTO crearTipoHabitacion(TipoHabitacionDTO tipoHabitacionDTO) {
        TipoHabitacion tipoHabitacion = tipoHabitacionMapper.toEntity(tipoHabitacionDTO);
        tipoHabitacion = tipoHabitacionRepository.save(tipoHabitacion);
        return tipoHabitacionMapper.toDTO(tipoHabitacion);
    }

    @Override
    public TipoHabitacionDTO actualizarTipoHabitacion(Long id, TipoHabitacionDTO tipoHabitacionDTO) {
        TipoHabitacion tipoHabitacion = tipoHabitacionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tipo de habitación no encontrado con id: " + id));
        tipoHabitacionMapper.updateEntityFromDTO(tipoHabitacionDTO, tipoHabitacion);
        tipoHabitacion = tipoHabitacionRepository.save(tipoHabitacion);
        return tipoHabitacionMapper.toDTO(tipoHabitacion);
    }

    @Override
    public void eliminarTipoHabitacion(Long id) {
        tipoHabitacionRepository.deleteById(id);
    }
}