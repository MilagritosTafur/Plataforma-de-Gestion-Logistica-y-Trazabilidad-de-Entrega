package com.example.demo.feature.cliente.controller;

import com.example.demo.feature.logistica.dto.*;
import com.example.demo.feature.logistica.service.ServicioLogistico;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ServicioLogistico servicioLogistico;

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody CrearClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioLogistico.crearCliente(request));
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public List<Map<String, Object>> listar() { return servicioLogistico.listarClientes(); }
    @PostMapping("/{clienteId}/direcciones")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Map<String, Object>> crearDireccion(@PathVariable Long clienteId, @Valid @RequestBody CrearDireccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioLogistico.crearDireccion(clienteId, request));
    }
    @GetMapping("/{clienteId}/direcciones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public List<Map<String, Object>> listarDirecciones(@PathVariable Long clienteId) { return servicioLogistico.listarDirecciones(clienteId); }
}
