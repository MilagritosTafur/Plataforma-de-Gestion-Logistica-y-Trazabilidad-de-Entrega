package com.example.demo.domain.repository;

import com.example.demo.domain.model.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuarioEmailIgnoreCase(String email);
    Optional<Cliente> findByEmailIgnoreCase(String email);
}
