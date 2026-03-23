package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.ClienteDTO;
import com.proyecto.hotel.model.entities.Cliente;
import com.proyecto.hotel.model.entities.Empresa;
import com.proyecto.hotel.model.mapper.ClienteMapper;
import com.proyecto.hotel.model.repository.ClienteRepository;
import com.proyecto.hotel.model.repository.EmpresaRepository;
import com.proyecto.hotel.model.repository.TipoDocumentoRepository;
import com.proyecto.hotel.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> obtenerTodosLosClientes() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO obtenerClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado con id: " + id));
        return clienteMapper.toDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        asignarTipoDocumento(cliente, clienteDTO);
        asignarEmpresa(cliente, clienteDTO.getEmpresaId());
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toDTO(clienteRepository.findById(cliente.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado con id: " + id));
        clienteMapper.updateEntityFromDTO(clienteDTO, cliente);
        asignarTipoDocumento(cliente, clienteDTO);
        asignarEmpresa(cliente, clienteDTO.getEmpresaId());
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toDTO(clienteRepository.findById(cliente.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    private void asignarEmpresa(Cliente cliente, Long empresaId) {
        if (empresaId != null) {
            Empresa empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> new BadRequestException("Empresa no encontrada con id: " + empresaId));
            cliente.setEmpresa(empresa);
        } else {
            cliente.setEmpresa(null);
        }
    }

    private void asignarTipoDocumento(Cliente cliente, ClienteDTO dto) {
        if (dto.getTipoDocumento() != null && dto.getTipoDocumento().getId() != null) {
            cliente.setTipoDocumento(
                    tipoDocumentoRepository.findById(dto.getTipoDocumento().getId())
                            .orElseThrow(() -> new BadRequestException("Tipo de documento no encontrado con id: " + dto.getTipoDocumento().getId()))
            );
        }
    }
}