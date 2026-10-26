package com.example.demo.Controller;

import com.example.demo.DTO.CrearRutaRequest;
import com.example.demo.Service.ServicioLogistico;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final ServicioLogistico servicioLogistico;

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Map<String, Object>> crearRuta(@Valid @RequestBody CrearRutaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioLogistico.crearRuta(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public ResponseEntity<List<Map<String, Object>>> listarRutas() {
        return ResponseEntity.ok(servicioLogistico.listarRutas());
    }
}
