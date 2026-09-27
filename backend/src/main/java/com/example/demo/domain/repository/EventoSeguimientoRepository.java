package com.example.demo.domain.repository;

import com.example.demo.domain.model.EventoSeguimiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoSeguimientoRepository extends JpaRepository<EventoSeguimiento, Long> {
    List<EventoSeguimiento> findByEnvioCodigoSeguimientoAndVisibleClienteTrueOrderByIdAsc(String codigoSeguimiento);
}
