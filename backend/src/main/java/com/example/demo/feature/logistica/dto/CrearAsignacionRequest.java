package com.example.demo.feature.logistica.dto;

import jakarta.validation.constraints.NotNull;

public record CrearAsignacionRequest(@NotNull Long envioId, @NotNull Long rutaId) { }
