package com.example.demo.Repository;

import com.example.demo.Entity.EventoSeguimiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoSeguimientoRepository extends JpaRepository<EventoSeguimiento, Long> {
    List<EventoSeguimiento> findByEnvioCodigoSeguimientoAndVisibleClienteTrueOrderByIdAsc(String codigoSeguimiento);
}
