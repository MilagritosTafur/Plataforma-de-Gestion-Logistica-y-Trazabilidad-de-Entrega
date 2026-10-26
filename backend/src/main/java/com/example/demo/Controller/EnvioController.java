package com.example.demo.Controller;

import com.example.demo.DTO.CrearEnvioRequest;
import com.example.demo.DTO.RegistrarEventoRequest;
import com.example.demo.Service.ServicioLogistico;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final ServicioLogistico servicioLogistico;

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Map<String, Object>> crearEnvio(@Valid @RequestBody CrearEnvioRequest request,
                                                           @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioLogistico.crearEnvio(request, principal.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public ResponseEntity<List<Map<String, Object>>> listarEnvios() {
        return ResponseEntity.ok(servicioLogistico.listarEnvios());
    }

    @GetMapping("/mis-envios")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<List<Map<String, Object>>> misEnvios(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(servicioLogistico.misEnvios(principal.getUsername()));
    }

    @PostMapping("/{id}/eventos")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public ResponseEntity<Map<String, Object>> registrarEvento(@PathVariable Long id, @Valid @RequestBody RegistrarEventoRequest request,
                                                                @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(servicioLogistico.registrarEvento(id, request, principal.getUsername()));
    }
}
