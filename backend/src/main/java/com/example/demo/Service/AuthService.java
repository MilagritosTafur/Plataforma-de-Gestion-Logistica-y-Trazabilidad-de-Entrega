package com.example.demo.Service;

import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.LoginResponse;
import com.example.demo.DTO.RegistroRequest;
import com.example.demo.DTO.UsuarioResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    UsuarioResponse registrarUsuario(RegistroRequest request);
}
