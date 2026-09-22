package com.example.demo.feature.envio.controller;

import com.example.demo.feature.envio.service.EnvioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    @PostMapping
    public ResponseEntity<Object> crearEnvio(@RequestBody Object request) {
        Object nuevoEnvio = envioService.crearEnvio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEnvio);
    }

    @GetMapping
    public ResponseEntity<List<Object>> listarEnvios() {
        return ResponseEntity.ok(envioService.listarEnvios());
    }

    @GetMapping("/{codigoSeguimiento}")
    public ResponseEntity<Object> consultarPorTracking(@PathVariable String codigoSeguimiento) {
        return ResponseEntity.ok(envioService.buscarPorCodigo(codigoSeguimiento));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Object> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        Object envioActualizado = envioService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(envioActualizado);
    }
}