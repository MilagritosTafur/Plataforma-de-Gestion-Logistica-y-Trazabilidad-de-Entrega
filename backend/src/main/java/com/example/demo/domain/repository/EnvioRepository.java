package com.example.demo.domain.repository;

import com.example.demo.domain.model.Envio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);
    List<Envio> findByClienteUsuarioEmailIgnoreCaseOrderByIdDesc(String email);
}
