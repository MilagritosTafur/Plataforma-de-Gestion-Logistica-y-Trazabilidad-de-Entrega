package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "vehiculos")
@Getter @Setter @NoArgsConstructor
public class Vehiculo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String placa;
    @Column(nullable = false) private String marca;
    private String modelo;
    @Column(name = "capacidad_kg", nullable = false) private BigDecimal capacidadKg;
    @Column(nullable = false) private boolean activo = true;
}
