package com.example.demo.feature.envio.service;

import java.util.List;

public interface EnvioService {
    Object crearEnvio(Object request);
    List<Object> listarEnvios();
    Object buscarPorCodigo(String codigo);
    Object actualizarEstado(Long id, String estado);
}
