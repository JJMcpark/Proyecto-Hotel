package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.EmpresaDTO;
import com.proyecto.hotel.model.entities.Empresa;
import com.proyecto.hotel.model.enums.EstadoAlquiler;
import com.proyecto.hotel.model.mapper.EmpresaMapper;
import com.proyecto.hotel.model.repository.AlquilerRepository;
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

    private static final String EMPRESA_PLACEHOLDER_RUC = "00000000000";

    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final AlquilerRepository alquilerRepository;
    private final EmpresaMapper empresaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDTO> obtenerTodasLasEmpresas() {
        return empresaRepository.findAll().stream()
                .filter(e -> !EMPRESA_PLACEHOLDER_RUC.equals(e.getRuc()))
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
        if (EMPRESA_PLACEHOLDER_RUC.equals(empresa.getRuc())) {
            throw new BadRequestException("No se puede modificar la empresa placeholder del sistema");
        }
        empresaMapper.updateEntityFromDTO(empresaDTO, empresa);
        return empresaMapper.toDTO(empresaRepository.save(empresa));
    }

    @Override
    @Transactional
    public void eliminarEmpresa(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Empresa no encontrada con id: " + id));

        if (EMPRESA_PLACEHOLDER_RUC.equals(empresa.getRuc())) {
            throw new BadRequestException("No se puede eliminar la empresa placeholder del sistema");
        }

        if (alquilerRepository.existsByEmpresaIdAndEstado(id, EstadoAlquiler.ACTIVO)) {
            throw new BadRequestException("No se puede eliminar la empresa porque tiene alquileres activos asociados");
        }

        Empresa placeholder = obtenerOcrearEmpresaPlaceholder();
        alquilerRepository.reasignarEmpresa(id, placeholder);
        clienteRepository.reasignarEmpresa(id, placeholder);

        empresaRepository.deleteById(id);
    }

    private Empresa obtenerOcrearEmpresaPlaceholder() {
        return empresaRepository.findByRuc(EMPRESA_PLACEHOLDER_RUC).orElseGet(() -> {
            Empresa placeholder = Empresa.builder()
                    .nombre("EMPRESA ELIMINADA")
                    .ruc(EMPRESA_PLACEHOLDER_RUC)
                    .telefono(null)
                    .build();
            return empresaRepository.save(placeholder);
        });
    }
}
