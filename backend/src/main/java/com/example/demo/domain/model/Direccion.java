package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "direcciones")
@Getter @Setter @NoArgsConstructor
public class Direccion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cliente_id", nullable = false) private Cliente cliente;
    @Column(nullable = false) private String direccion;
    @Column(nullable = false) private String distrito;
    @Column(nullable = false) private String ciudad;
    private String referencia;
    @Column(name = "principal", nullable = false) private boolean principal;
}
