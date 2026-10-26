package com.example.demo.DTO;

import com.example.demo.Entity.EstadoEnvio;
import jakarta.validation.constraints.NotNull;

public record RegistrarEventoRequest(
        @NotNull EstadoEnvio nuevoEstado,
        String ubicacion,
        String observacion,
        String receptorNombre
) { }
