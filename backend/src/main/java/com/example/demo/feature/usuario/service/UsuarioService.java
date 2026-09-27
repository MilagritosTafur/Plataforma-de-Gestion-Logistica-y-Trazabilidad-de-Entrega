package com.example.demo.feature.usuario.service;

import com.example.demo.feature.usuario.dto.CrearCuentaRequest;
import com.example.demo.feature.usuario.dto.UsuarioResponse;
import java.util.List;

public interface UsuarioService {

    UsuarioResponse crearCuenta(CrearCuentaRequest request, String emailAdminCreador);

    List<UsuarioResponse> listarEquipo();
}
