package com.example.demo.feature.usuario.dto;

import com.example.demo.domain.model.RolNombre;

public record UsuarioResponse(
        Long id,
        String email,
        String nombre,
        String apellido,
        String telefono,
        RolNombre rol,
        boolean activo
) {
}
