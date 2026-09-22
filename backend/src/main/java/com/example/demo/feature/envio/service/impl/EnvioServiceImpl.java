package com.example.demo.feature.envio.service.impl;


import com.example.demo.feature.envio.service.EnvioService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EnvioServiceImpl implements EnvioService {

    @Override
    public Object crearEnvio(Object request) {
        return null;
    }

    @Override
    public List<Object> listarEnvios() {
        return List.of();
    }

    @Override
    public Object buscarPorCodigo(String codigo) {
        return null;
    }

    @Override
    public Object actualizarEstado(Long id, String estado) {
        return null;
    }
}