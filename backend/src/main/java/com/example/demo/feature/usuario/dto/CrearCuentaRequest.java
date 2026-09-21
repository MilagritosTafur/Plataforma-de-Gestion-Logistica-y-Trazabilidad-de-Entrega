package com.example.demo.feature.usuario.dto;

import com.example.demo.domain.model.RolNombre;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearCuentaRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "La contrasena debe tener al menos 8 caracteres") String password,
        @NotBlank String nombre,
        @NotBlank String apellido,
        String telefono,
        @NotNull RolNombre rol
) {
}
