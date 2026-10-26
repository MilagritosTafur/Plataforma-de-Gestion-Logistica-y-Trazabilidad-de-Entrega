package com.example.demo.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CrearClienteRequest(
        @NotBlank String documento,
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank @Email String email,
        String telefono
) { }
