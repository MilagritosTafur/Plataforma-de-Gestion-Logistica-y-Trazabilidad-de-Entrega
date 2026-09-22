package com.example.demo.feature.tracking.service.impl;


import com.example.demo.feature.tracking.service.TrackingService;
import org.springframework.stereotype.Service;

@Service
public class TrackingServiceImpl implements TrackingService {

    @Override
    public Object obtenerHistorialPorCodigo(String codigoSeguimiento) {
        // Lógica para buscar el envío y sus eventos de seguimiento en la base de datos
        return null;
    }
}