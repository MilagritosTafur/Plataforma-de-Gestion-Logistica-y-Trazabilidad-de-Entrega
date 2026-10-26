package com.example.demo.DTO;

import com.example.demo.Entity.RolNombre;

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
