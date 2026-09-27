package com.example.demo.domain.repository;

import com.example.demo.domain.model.Direccion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByClienteId(Long clienteId);
}
