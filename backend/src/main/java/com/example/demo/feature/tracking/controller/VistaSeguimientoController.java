package com.example.demo.feature.tracking.controller;

import com.example.demo.feature.tracking.service.TrackingService;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/** Renderiza el seguimiento público desde el servidor; la API REST permanece disponible en /api/tracking. */
@Controller
@RequiredArgsConstructor
public class VistaSeguimientoController {

    private final TrackingService trackingService;

    @GetMapping("/seguimiento")
    public String mostrarSeguimiento(
            @RequestParam(required = false) String codigo,
            Model model) {
        String codigoNormalizado = codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT);
        model.addAttribute("codigo", codigoNormalizado);

        if (codigoNormalizado.isBlank()) {
            return "seguimiento";
        }

        try {
            model.addAttribute("envio", trackingService.obtenerHistorialPorCodigo(codigoNormalizado));
        } catch (ResponseStatusException ex) {
            model.addAttribute("error", ex.getReason());
        }
        return "seguimiento";
    }
}
