package com.example.demo.Service;

import com.example.demo.DTO.CrearCuentaRequest;
import com.example.demo.DTO.UsuarioResponse;
import java.util.List;

public interface UsuarioService {

    UsuarioResponse crearCuenta(CrearCuentaRequest request, String emailAdminCreador);

    List<UsuarioResponse> listarEquipo();
}
