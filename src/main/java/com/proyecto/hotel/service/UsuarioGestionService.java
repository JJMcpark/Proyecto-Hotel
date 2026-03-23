package com.proyecto.hotel.service;

import com.proyecto.hotel.controller.request.ActualizarPerfilRequestDTO;
import com.proyecto.hotel.controller.request.ActualizarRecepcionistaRequestDTO;
import com.proyecto.hotel.controller.request.CambiarPasswordRequestDTO;
import com.proyecto.hotel.controller.request.CrearRecepcionistaRequestDTO;
import com.proyecto.hotel.controller.request.ResetPasswordRequestDTO;
import com.proyecto.hotel.model.dto.UsuarioDTO;

import java.util.List;

public interface UsuarioGestionService {
    UsuarioDTO obtenerMiPerfil(String numDocumento);
    UsuarioDTO actualizarMiPerfil(String numDocumento, ActualizarPerfilRequestDTO request);
    void cambiarMiPassword(String numDocumento, CambiarPasswordRequestDTO request);

    List<UsuarioDTO> listarRecepcionistas();
    UsuarioDTO obtenerRecepcionistaPorId(Long id);
    UsuarioDTO crearRecepcionista(CrearRecepcionistaRequestDTO request);
    UsuarioDTO actualizarRecepcionista(Long id, ActualizarRecepcionistaRequestDTO request);
    void resetearPasswordRecepcionista(Long id, ResetPasswordRequestDTO request);
}
