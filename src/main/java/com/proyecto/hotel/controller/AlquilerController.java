package com.proyecto.hotel.controller;

import com.proyecto.hotel.service.AlquilerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.proyecto.hotel.controller.request.CheckInRequestDTO;
import com.proyecto.hotel.controller.response.AlquilerResponseDTO;
import com.proyecto.hotel.model.enums.MetodoPago;

import java.util.List;

@RestController
@RequestMapping("/api/recepcion/alquiler")
@RequiredArgsConstructor
public class AlquilerController {

    private final AlquilerService alquilerService;

    @PostMapping("/check-in")
    public ResponseEntity<AlquilerResponseDTO> checkIn(
            @Valid @RequestBody CheckInRequestDTO dto, 
            Authentication auth) {
        return ResponseEntity.ok(alquilerService.registrarCheckIn(dto, auth.getName()));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<Void> checkOut(
            @PathVariable Long id, 
            @RequestParam MetodoPago metodoPago, 
            Authentication auth) {
        alquilerService.registrarCheckOut(id, auth.getName(), metodoPago);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activos")
    public ResponseEntity<List<AlquilerResponseDTO>> listarActivos() {
        return ResponseEntity.ok(alquilerService.listarAlquileresActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlquilerResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.obtenerAlquilerPorId(id));
    }
}