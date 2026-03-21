package com.proyecto.hotel.service;

import com.proyecto.hotel.model.dto.EmpresaDTO;

import java.util.List;

public interface EmpresaService {
    List<EmpresaDTO> obtenerTodasLasEmpresas();
    EmpresaDTO obtenerEmpresaPorId(Long id);
    EmpresaDTO crearEmpresa(EmpresaDTO empresaDTO);
    EmpresaDTO actualizarEmpresa(Long id, EmpresaDTO empresaDTO);
    void eliminarEmpresa(Long id);
}
