package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.HabitacionDTO;
import com.proyecto.hotel.model.entities.Habitacion;
import com.proyecto.hotel.model.enums.EstadoHabitacion;
import com.proyecto.hotel.model.mapper.HabitacionMapper;
import com.proyecto.hotel.model.repository.HabitacionRepository;
import com.proyecto.hotel.service.HabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final HabitacionMapper habitacionMapper;

    @Override
    public List<HabitacionDTO> obtenerTodasLasHabitaciones() {
        return habitacionRepository.findAll().stream()
                .map(habitacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HabitacionDTO> obtenerHabitacionesDisponibles() {
        return habitacionRepository.findByEstado(EstadoHabitacion.DISPONIBLE.name()).stream()
                .map(habitacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HabitacionDTO> obtenerHabitacionesPorEstado(String estado) {
        try {
            EstadoHabitacion.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Estado de habitación inválido: " + estado);
        }

        return habitacionRepository.findByEstado(estado.toUpperCase()).stream()
                .map(habitacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HabitacionDTO> obtenerHabitacionesPorTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new BadRequestException("Tipo de habitación es requerido");
        }

        return habitacionRepository.findByTipoHabitacionNombre(tipo.trim().toUpperCase()).stream()
                .map(habitacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HabitacionDTO obtenerHabitacionPorId(Long id) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));
        return habitacionMapper.toDTO(habitacion);
    }

    @Override
    public HabitacionDTO crearHabitacion(HabitacionDTO habitacionDTO) {
        Habitacion habitacion = habitacionMapper.toEntity(habitacionDTO);
        habitacion = habitacionRepository.save(habitacion);
        return habitacionMapper.toDTO(habitacion);
    }

    @Override
    public HabitacionDTO actualizarHabitacion(Long id, HabitacionDTO habitacionDTO) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));
        habitacionMapper.updateEntityFromDTO(habitacionDTO, habitacion);
        habitacion = habitacionRepository.save(habitacion);
        return habitacionMapper.toDTO(habitacion);
    }

    @Override
    public void eliminarHabitacion(Long id) {
        habitacionRepository.deleteById(id);
    }
}