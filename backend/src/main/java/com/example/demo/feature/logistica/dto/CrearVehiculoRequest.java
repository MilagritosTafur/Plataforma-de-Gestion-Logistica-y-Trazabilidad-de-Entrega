package com.example.demo.feature.logistica.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CrearVehiculoRequest(
        @NotBlank String placa,
        @NotBlank String marca,
        String modelo,
        @DecimalMin(value = "0.1") BigDecimal capacidadKg
) { }
