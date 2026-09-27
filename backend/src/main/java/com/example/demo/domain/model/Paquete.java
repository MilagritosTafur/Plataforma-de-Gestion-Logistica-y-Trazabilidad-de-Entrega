package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "paquetes")
@Getter @Setter @NoArgsConstructor
public class Paquete {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "envio_id", nullable = false) private Envio envio;
    @Column(nullable = false) private String descripcion;
    @Column(name = "peso_kg", nullable = false) private BigDecimal pesoKg;
    @Column(nullable = false) private Integer cantidad = 1;
}
