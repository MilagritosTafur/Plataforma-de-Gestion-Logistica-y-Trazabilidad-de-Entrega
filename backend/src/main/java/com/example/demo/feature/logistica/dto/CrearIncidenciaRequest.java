package com.example.demo.feature.logistica.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearIncidenciaRequest(@NotBlank String descripcion, String evidenciaUrl, String ubicacion) { }
