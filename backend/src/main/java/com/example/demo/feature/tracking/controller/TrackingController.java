package com.example.demo.feature.tracking.controller;

import com.example.demo.feature.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping("/{codigoSeguimiento}")
    public ResponseEntity<Object> consultarTrazabilidad(@PathVariable String codigoSeguimiento) {
        Object trazabilidad = trackingService.obtenerHistorialPorCodigo(codigoSeguimiento);
        return ResponseEntity.ok(trazabilidad);
    }
}