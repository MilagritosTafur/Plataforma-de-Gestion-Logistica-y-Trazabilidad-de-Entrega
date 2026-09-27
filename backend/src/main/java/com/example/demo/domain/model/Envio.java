package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
@Getter @Setter @NoArgsConstructor
public class Envio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "codigo_seguimiento", nullable = false, unique = true) private String codigoSeguimiento;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cliente_id", nullable = false) private Cliente cliente;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "direccion_destino_id", nullable = false) private Direccion direccionDestino;
    @Column(nullable = false) private String descripcion;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private EstadoEnvio estado = EstadoEnvio.REGISTRADO;
    @Column(name = "fecha_estimada_entrega") private LocalDate fechaEstimadaEntrega;
    @Column(name = "fecha_entrega") private LocalDateTime fechaEntrega;
    @Column(name = "receptor_nombre") private String receptorNombre;
}
