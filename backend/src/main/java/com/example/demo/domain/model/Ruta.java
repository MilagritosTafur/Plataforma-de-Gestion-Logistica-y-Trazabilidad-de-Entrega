package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "rutas")
@Getter @Setter @NoArgsConstructor
public class Ruta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String nombre;
    @Column(name = "fecha_programada", nullable = false) private LocalDate fechaProgramada;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "repartidor_id", nullable = false) private Repartidor repartidor;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vehiculo_id", nullable = false) private Vehiculo vehiculo;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private EstadoRuta estado = EstadoRuta.PROGRAMADA;
}
