package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CrearRutaRequest(
        @NotBlank String nombre,
        @NotNull LocalDate fechaProgramada,
        @NotNull Long repartidorId,
        @NotNull Long vehiculoId
) { }
