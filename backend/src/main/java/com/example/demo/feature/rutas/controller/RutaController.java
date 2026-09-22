package com.example.demo.feature.rutas.controller;

import com.example.demo.feature.rutas.service.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    @PostMapping
    public ResponseEntity<Object> crearRuta(@RequestBody Object request) {
        Object nuevaRuta = rutaService.crearRuta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaRuta);
    }

    @GetMapping
    public ResponseEntity<List<Object>> listarRutas() {
        return ResponseEntity.ok(rutaService.listarRutas());
    }

    @PostMapping("/{rutaId}/asignar/{repartidorId}")
    public ResponseEntity<Object> asignarRutaARepartidor(@PathVariable Long rutaId, @PathVariable Long repartidorId) {
        Object asignacion = rutaService.asignarRepartidor(rutaId, repartidorId);
        return ResponseEntity.ok(asignacion);
    }
}