package com.example.demo.feature.rutas.service;


import java.util.List;

public interface RutaService {
    Object crearRuta(Object request);
    List<Object> listarRutas();
    Object asignarRepartidor(Long rutaId, Long repartidorId);
}
