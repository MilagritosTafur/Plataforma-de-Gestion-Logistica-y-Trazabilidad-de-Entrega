package com.example.demo.feature.rutas.service.impl;

import com.example.demo.feature.rutas.service.RutaService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RutaServiceImpl implements RutaService {

    @Override
    public Object crearRuta(Object request) {
        return null;
    }

    @Override
    public List<Object> listarRutas() {
        return List.of();
    }

    @Override
    public Object asignarRepartidor(Long rutaId, Long repartidorId) {
        return null;
    }
}
