package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;

public record CrearIncidenciaRequest(
    //Este es un record  (una forma moderna de crear clases inmutables en Java)
    @NotBlank String descripcion, 
    String evidenciaUrl, 
    String ubicacion
) {
}
