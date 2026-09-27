package com.example.demo.domain.repository;

import com.example.demo.domain.model.Asignacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    boolean existsByEnvioIdAndActivaTrue(Long envioId);
    Optional<Asignacion> findByEnvioIdAndActivaTrue(Long envioId);
    List<Asignacion> findByRutaRepartidorUsuarioEmailIgnoreCaseAndActivaTrueOrderByIdDesc(String email);
    long countByRutaIdAndActivaTrue(Long rutaId);
}
