package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.ClienteDTO;
import com.proyecto.hotel.model.entities.Cliente;
import com.proyecto.hotel.model.entities.Empresa;
import com.proyecto.hotel.model.enums.EstadoAlquiler;
import com.proyecto.hotel.model.mapper.ClienteMapper;
import com.proyecto.hotel.model.repository.AlquilerRepository;
import com.proyecto.hotel.model.repository.ClienteRepository;
import com.proyecto.hotel.model.repository.EmpresaRepository;
import com.proyecto.hotel.model.repository.MovimientoCajaRepository;
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

    private static final String CLIENTE_PLACEHOLDER_DOC = "ELIMINADO_SYS";

    private final ClienteRepository clienteRepository;
    private final AlquilerRepository alquilerRepository;
    private final EmpresaRepository empresaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> obtenerTodosLosClientes() {
        return clienteRepository.findAll().stream()
                .filter(cliente -> !CLIENTE_PLACEHOLDER_DOC.equals(cliente.getNumDocumento()))
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO obtenerClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado con id: " + id));
        if (CLIENTE_PLACEHOLDER_DOC.equals(cliente.getNumDocumento())) {
            throw new BadRequestException("Cliente no encontrado con id: " + id);
        }
        return clienteMapper.toDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        String numDoc = clienteDTO.getNumDocumento() != null ? clienteDTO.getNumDocumento().trim() : null;
        if (numDoc != null && clienteRepository.existsByNumDocumento(numDoc)) {
            throw new BadRequestException("El número de documento ya está registrado");
        }
        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        asignarTipoDocumento(cliente, clienteDTO);
        asignarEmpresa(cliente, clienteDTO.getEmpresaId());
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toDTO(clienteRepository.findById(cliente.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO, boolean permitirCambioEmpresa) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado con id: " + id));
        if (CLIENTE_PLACEHOLDER_DOC.equals(cliente.getNumDocumento())) {
            throw new BadRequestException("Cliente no encontrado con id: " + id);
        }

        String numDoc = clienteDTO.getNumDocumento() != null ? clienteDTO.getNumDocumento().trim() : null;
        if (numDoc != null && clienteRepository.existsByNumDocumentoAndIdNot(numDoc, id)) {
            throw new BadRequestException("El número de documento ya está registrado por otro cliente");
        }

        Long empresaActualId = cliente.getEmpresa() != null ? cliente.getEmpresa().getId() : null;
        Long empresaSolicitadaId = clienteDTO.getEmpresaId();
        boolean empresaCambio = (empresaActualId == null && empresaSolicitadaId != null)
                || (empresaActualId != null && !empresaActualId.equals(empresaSolicitadaId));

        if (!permitirCambioEmpresa && empresaCambio) {
            throw new BadRequestException("Solo el administrador puede cambiar la afiliación a empresa de un cliente");
        }

        clienteMapper.updateEntityFromDTO(clienteDTO, cliente);
        asignarTipoDocumento(cliente, clienteDTO);
        asignarEmpresa(cliente, clienteDTO.getEmpresaId());
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toDTO(clienteRepository.findById(cliente.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null || CLIENTE_PLACEHOLDER_DOC.equals(cliente.getNumDocumento())) {
            throw new BadRequestException("Cliente no encontrado con id: " + id);
        }

        long activos = alquilerRepository.countByClienteIdAndEstado(id, EstadoAlquiler.ACTIVO);
        if (activos > 0) {
            throw new BadRequestException("No se puede eliminar el cliente porque tiene alquileres activos asociados");
        }

        long historial = alquilerRepository.countByClienteIdAndEstado(id, EstadoAlquiler.FINALIZADO);
        if (historial > 0) {
            movimientoCajaRepository.desvincularAlquilerPorClienteId(id, EstadoAlquiler.FINALIZADO);
            alquilerRepository.eliminarPorClienteIdYEstado(id, EstadoAlquiler.FINALIZADO);
        }

        alquilerRepository.eliminarHuespedPorClienteId(id);
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