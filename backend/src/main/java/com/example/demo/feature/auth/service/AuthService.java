package com.example.demo.feature.auth.service;

import com.example.demo.feature.auth.dto.LoginRequest;
import com.example.demo.feature.auth.dto.LoginResponse;
import com.example.demo.feature.auth.dto.RegistroRequest;
import com.example.demo.feature.usuario.dto.UsuarioResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    UsuarioResponse registrarUsuario(RegistroRequest request);
}
