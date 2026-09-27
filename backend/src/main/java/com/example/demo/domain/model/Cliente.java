package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter @Setter @NoArgsConstructor
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    private String documento;
    @Column(nullable = false) private String nombres;
    @Column(nullable = false) private String apellidos;
    @Column(nullable = false) private String email;
    private String telefono;
    @Column(nullable = false) private boolean activo = true;
}
