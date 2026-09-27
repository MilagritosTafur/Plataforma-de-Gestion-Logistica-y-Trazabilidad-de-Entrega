package com.example.demo.domain.repository;

import com.example.demo.domain.model.Repartidor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {
    Optional<Repartidor> findByUsuarioEmailIgnoreCase(String email);
    boolean existsByUsuarioId(Long usuarioId);
    boolean existsByDocumentoIgnoreCase(String documento);
    boolean existsByLicenciaConducirIgnoreCase(String licenciaConducir);
}
