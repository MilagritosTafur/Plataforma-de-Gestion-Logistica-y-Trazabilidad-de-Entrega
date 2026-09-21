package com.example.demo.feature.usuario.service;

import com.example.demo.feature.usuario.dto.CrearCuentaRequest;
import com.example.demo.feature.usuario.dto.UsuarioResponse;

public interface UsuarioService {

    UsuarioResponse crearCuenta(CrearCuentaRequest request, String emailAdminCreador);
}
