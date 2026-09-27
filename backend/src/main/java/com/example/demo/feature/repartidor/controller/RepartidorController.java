package com.example.demo.feature.repartidor.controller;

import com.example.demo.feature.logistica.service.ServicioLogistico;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repartidores")
@RequiredArgsConstructor
public class RepartidorController {
    private final ServicioLogistico servicioLogistico;
    @GetMapping @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public List<Map<String, Object>> listar() { return servicioLogistico.listarRepartidores(); }
}
