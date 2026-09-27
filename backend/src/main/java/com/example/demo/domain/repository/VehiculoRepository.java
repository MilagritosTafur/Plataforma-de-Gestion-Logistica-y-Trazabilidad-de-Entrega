package com.example.demo.domain.repository;

import com.example.demo.domain.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> { }
