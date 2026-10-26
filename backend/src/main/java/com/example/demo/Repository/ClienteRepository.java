package com.example.demo.Repository;

import com.example.demo.Entity.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuarioEmailIgnoreCase(String email);
    Optional<Cliente> findByEmailIgnoreCase(String email);
}
