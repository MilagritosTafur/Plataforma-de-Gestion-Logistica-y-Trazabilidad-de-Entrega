package com.example.demo.DTO;


public record LoginResponse(
        String token,
        String tokenType,
        long expiresInMs,
        UsuarioResponse usuario
) {
}
