package com.example.demo.feature.usuario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.feature.usuario.dto.CrearCuentaRequest;
import com.example.demo.feature.usuario.dto.UsuarioResponse;
import com.example.demo.feature.usuario.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponse> crearCuenta(@Valid @RequestBody CrearCuentaRequest request,
                                                         @AuthenticationPrincipal UserDetails principal) {
        UsuarioResponse usuario = usuarioService.crearCuenta(request, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
}
