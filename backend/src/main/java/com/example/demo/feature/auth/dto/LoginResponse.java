package com.example.demo.feature.auth.dto;

import com.example.demo.feature.usuario.dto.UsuarioResponse;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInMs,
        UsuarioResponse usuario
) {
}
