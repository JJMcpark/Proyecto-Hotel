package com.proyecto.hotel.service.impl;

import com.proyecto.hotel.controller.request.ActualizarPerfilRequestDTO;
import com.proyecto.hotel.controller.request.ActualizarRecepcionistaRequestDTO;
import com.proyecto.hotel.controller.request.CambiarPasswordRequestDTO;
import com.proyecto.hotel.controller.request.CrearRecepcionistaRequestDTO;
import com.proyecto.hotel.controller.request.ResetPasswordRequestDTO;
import com.proyecto.hotel.handler.BadRequestException;
import com.proyecto.hotel.model.dto.UsuarioDTO;
import com.proyecto.hotel.model.entities.Rol;
import com.proyecto.hotel.model.entities.TipoDocumento;
import com.proyecto.hotel.model.entities.Usuario;
import com.proyecto.hotel.model.mapper.UsuarioMapper;
import com.proyecto.hotel.model.repository.RolRepository;
import com.proyecto.hotel.model.repository.TipoDocumentoRepository;
import com.proyecto.hotel.model.repository.UsuarioRepository;
import com.proyecto.hotel.service.UsuarioGestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioGestionServiceImpl implements UsuarioGestionService {

    private static final String ROLE_RECEPCIONISTA = "ROLE_RECEPCIONISTA";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerMiPerfil(String numDocumento) {
        Usuario usuario = buscarUsuarioPorDocumento(numDocumento);
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarMiPerfil(String numDocumento, ActualizarPerfilRequestDTO request) {
        Usuario usuario = buscarUsuarioPorDocumento(numDocumento);
        usuario.setNombre(request.getNombre());
        usuario.setTelefono(request.getTelefono());

        if (request.getNumDocumento() != null && !request.getNumDocumento().equals(numDocumento)) {
            if (usuarioRepository.existsByNumDocumento(request.getNumDocumento())) {
                throw new BadRequestException("Ya existe un usuario con el número de documento: " + request.getNumDocumento());
            }
            usuario.setNumDocumento(request.getNumDocumento());
        }

        if (request.getTipoDocumento() != null) {
            usuario.setTipoDocumento(buscarTipoDocumentoPorNombre(request.getTipoDocumento()));
        }

        return usuarioMapper.toDTO(usuarioRepository.findUsuarioById(usuarioRepository.save(usuario).getId()).orElseThrow());
    }

    @Override
    @Transactional
    public void cambiarMiPassword(String numDocumento, CambiarPasswordRequestDTO request) {
        Usuario usuario = buscarUsuarioPorDocumento(numDocumento);

        if (!passwordEncoder.matches(request.getCurrentPassword(), usuario.getPassword())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarRecepcionistas() {
        return usuarioRepository.findByRolNombreOrderByNombreAsc(ROLE_RECEPCIONISTA).stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerRecepcionistaPorId(Long id) {
        Usuario usuario = buscarRecepcionistaPorId(id);
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO crearRecepcionista(CrearRecepcionistaRequestDTO request) {
        if (usuarioRepository.existsByNumDocumento(request.getNumDocumento())) {
            throw new BadRequestException("Ya existe un usuario con el número de documento: " + request.getNumDocumento());
        }

        Rol rolRecepcionista = rolRepository.findByNombre(ROLE_RECEPCIONISTA)
                .orElseThrow(() -> new BadRequestException("No existe el rol de recepcionista configurado"));

        TipoDocumento tipoDocumento = buscarTipoDocumentoPorNombre(request.getTipoDocumento());

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .numDocumento(request.getNumDocumento())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .tipoDocumento(tipoDocumento)
                .rol(rolRecepcionista)
                .build();

        return usuarioMapper.toDTO(usuarioRepository.findUsuarioById(usuarioRepository.save(usuario).getId()).orElseThrow());
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarRecepcionista(Long id, ActualizarRecepcionistaRequestDTO request) {
        Usuario usuario = buscarRecepcionistaPorId(id);
        TipoDocumento tipoDocumento = buscarTipoDocumentoPorNombre(request.getTipoDocumento());

        usuario.setNombre(request.getNombre());
        usuario.setTelefono(request.getTelefono());
        usuario.setTipoDocumento(tipoDocumento);

        return usuarioMapper.toDTO(usuarioRepository.findUsuarioById(usuarioRepository.save(usuario).getId()).orElseThrow());
    }

    @Override
    @Transactional
    public void resetearPasswordRecepcionista(Long id, ResetPasswordRequestDTO request) {
        Usuario usuario = buscarRecepcionistaPorId(id);
        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);
    }

    private Usuario buscarUsuarioPorDocumento(String numDocumento) {
        return usuarioRepository.findByNumDocumento(numDocumento)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado: " + numDocumento));
    }

    private Usuario buscarRecepcionistaPorId(Long id) {
        return usuarioRepository.findByIdAndRolNombre(id, ROLE_RECEPCIONISTA)
                .orElseThrow(() -> new BadRequestException("Recepcionista no encontrado con id: " + id));
    }

    private TipoDocumento buscarTipoDocumentoPorNombre(String nombre) {
        return tipoDocumentoRepository.findByNombre(nombre)
                .orElseThrow(() -> new BadRequestException("Tipo de documento no encontrado: '" + nombre + "'. Verifique los valores disponibles."));
    }
}
