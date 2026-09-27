package com.example.demo.feature.asignacion.controller;

import com.example.demo.feature.logistica.dto.CrearAsignacionRequest;
import com.example.demo.feature.logistica.service.ServicioLogistico;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {
    private final ServicioLogistico servicioLogistico;
    @PostMapping @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody CrearAsignacionRequest request,
                                                     @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioLogistico.asignarEnvio(request, principal.getUsername()));
    }
    @GetMapping("/mias") @PreAuthorize("hasRole('REPARTIDOR')")
    public List<Map<String, Object>> mias(@AuthenticationPrincipal UserDetails principal) {
        return servicioLogistico.misAsignaciones(principal.getUsername());
    }
}
