package com.example.demo.feature.logistica.dto;

import com.example.demo.domain.model.EstadoEnvio;
import jakarta.validation.constraints.NotNull;

public record RegistrarEventoRequest(
        @NotNull EstadoEnvio nuevoEstado,
        String ubicacion,
        String observacion,
        String receptorNombre
) { }
