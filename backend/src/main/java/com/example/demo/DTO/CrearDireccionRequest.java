package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;

public record CrearDireccionRequest(
        @NotBlank String direccion,
        @NotBlank String distrito,
        @NotBlank String ciudad,
        String referencia,
        boolean principal
) { }
