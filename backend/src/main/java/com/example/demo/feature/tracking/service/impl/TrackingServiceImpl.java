package com.example.demo.feature.tracking.service.impl;

import com.example.demo.feature.logistica.service.ServicioLogistico;
import com.example.demo.feature.tracking.service.TrackingService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Adaptador del módulo Tracking: reutiliza las reglas y datos autorizados de logística. */
@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final ServicioLogistico servicioLogistico;

    @Override
    public Map<String, Object> obtenerHistorialPorCodigo(String codigoSeguimiento) {
        return servicioLogistico.trackingPublico(codigoSeguimiento);
    }
}
