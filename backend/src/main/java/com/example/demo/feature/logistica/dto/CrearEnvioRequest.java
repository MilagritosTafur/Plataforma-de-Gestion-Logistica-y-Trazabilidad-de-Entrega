package com.example.demo.feature.logistica.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CrearEnvioRequest(
        @NotNull Long clienteId,
        @NotNull Long direccionDestinoId,
        @NotBlank String descripcion,
        @NotBlank String descripcionPaquete,
        @NotNull @DecimalMin(value = "0.1") BigDecimal pesoKg,
        @NotNull @DecimalMin(value = "1") Integer cantidad,
        LocalDate fechaEstimadaEntrega
) { }
