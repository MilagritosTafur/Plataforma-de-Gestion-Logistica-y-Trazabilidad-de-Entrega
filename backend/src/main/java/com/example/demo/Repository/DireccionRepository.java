package com.example.demo.Repository;

import com.example.demo.Entity.Direccion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByClienteId(Long clienteId);
}
