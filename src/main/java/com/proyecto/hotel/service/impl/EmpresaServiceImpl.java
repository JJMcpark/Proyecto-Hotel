package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.EmpresaDTO;
import com.proyecto.hotel.model.entities.Empresa;
import com.proyecto.hotel.model.mapper.EmpresaMapper;
import com.proyecto.hotel.model.repository.ClienteRepository;
import com.proyecto.hotel.model.repository.EmpresaRepository;
import com.proyecto.hotel.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final EmpresaMapper empresaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDTO> obtenerTodasLasEmpresas() {
        return empresaRepository.findAll().stream()
                .map(empresaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaDTO obtenerEmpresaPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Empresa no encontrada con id: " + id));
        return empresaMapper.toDTO(empresa);
    }

    @Override
    @Transactional
    public EmpresaDTO crearEmpresa(EmpresaDTO empresaDTO) {
        if (empresaRepository.existsByRuc(empresaDTO.getRuc())) {
            throw new BadRequestException("Ya existe una empresa con el RUC: " + empresaDTO.getRuc());
        }
        Empresa empresa = empresaMapper.toEntity(empresaDTO);
        return empresaMapper.toDTO(empresaRepository.save(empresa));
    }

    @Override
    @Transactional
    public EmpresaDTO actualizarEmpresa(Long id, EmpresaDTO empresaDTO) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Empresa no encontrada con id: " + id));
        empresaMapper.updateEntityFromDTO(empresaDTO, empresa);
        return empresaMapper.toDTO(empresaRepository.save(empresa));
    }

    @Override
    @Transactional
    public void eliminarEmpresa(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new BadRequestException("Empresa no encontrada con id: " + id);
        }
        if (clienteRepository.existsByEmpresaId(id)) {
            throw new BadRequestException("No se puede eliminar la empresa porque tiene clientes asociados.");
        }
        empresaRepository.deleteById(id);
    }
}
