package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;

public record CrearIncidenciaRequest(@NotBlank String descripcion, String evidenciaUrl, String ubicacion) { }
