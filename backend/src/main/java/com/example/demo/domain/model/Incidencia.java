package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "incidencias")
@Getter @Setter @NoArgsConstructor
public class Incidencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "envio_id", nullable = false) private Envio envio;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "repartidor_id", nullable = false) private Repartidor repartidor;
    @Column(nullable = false) private String descripcion;
    @Column(name = "evidencia_url") private String evidenciaUrl;
    @Column(nullable = false) private boolean resuelta;
}
