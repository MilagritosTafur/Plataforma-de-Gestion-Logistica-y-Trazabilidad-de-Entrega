package com.example.demo.Controller;

import com.example.demo.DTO.CrearIncidenciaRequest;
import com.example.demo.Service.ServicioLogistico;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {
    private final ServicioLogistico servicioLogistico;
    @PostMapping("/envio/{envioId}") @PreAuthorize("hasRole('REPARTIDOR')")
    public ResponseEntity<Map<String, Object>> crear(@PathVariable Long envioId, @Valid @RequestBody CrearIncidenciaRequest request,
                                                     @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(servicioLogistico.reportarIncidencia(envioId, request, principal.getUsername()));
    }
}
