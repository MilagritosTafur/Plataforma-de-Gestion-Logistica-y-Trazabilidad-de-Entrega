package com.example.demo.feature.vehiculo.controller;

import com.example.demo.feature.logistica.dto.CrearVehiculoRequest;
import com.example.demo.feature.logistica.service.ServicioLogistico;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {
    private final ServicioLogistico servicioLogistico;
    @PostMapping @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody CrearVehiculoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioLogistico.crearVehiculo(request));
    }
    @GetMapping @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public List<Map<String, Object>> listar() { return servicioLogistico.listarVehiculos(); }
}
