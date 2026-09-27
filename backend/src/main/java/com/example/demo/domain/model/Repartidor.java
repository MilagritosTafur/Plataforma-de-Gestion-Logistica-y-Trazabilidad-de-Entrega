package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "repartidores")
@Getter @Setter @NoArgsConstructor
public class Repartidor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "usuario_id", nullable = false) private Usuario usuario;
    private String documento;
    @Column(name = "licencia_conducir") private String licenciaConducir;
    @Column(nullable = false) private boolean disponible = true;
}
