package com.example.demo.feature.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.feature.auth.dto.LoginRequest;
import com.example.demo.feature.auth.dto.LoginResponse;
import com.example.demo.feature.auth.dto.RegistroRequest;
import com.example.demo.feature.auth.service.AuthService;
import com.example.demo.feature.usuario.dto.UsuarioResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registro(@Valid @RequestBody RegistroRequest request) {
        UsuarioResponse usuario = authService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
}
