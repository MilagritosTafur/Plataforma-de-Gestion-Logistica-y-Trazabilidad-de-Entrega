package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignaciones")
@Getter @Setter @NoArgsConstructor
public class Asignacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "envio_id", nullable = false) private Envio envio;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "ruta_id", nullable = false) private Ruta ruta;
    @Column(nullable = false) private boolean activa = true;
    @Column(name = "finalizada_at") private LocalDateTime finalizadaAt;
}
