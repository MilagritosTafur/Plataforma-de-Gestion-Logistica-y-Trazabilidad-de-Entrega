package com.example.demo.DTO;

import jakarta.validation.constraints.NotNull;

public record CrearAsignacionRequest(@NotNull Long envioId, @NotNull Long rutaId) { }
