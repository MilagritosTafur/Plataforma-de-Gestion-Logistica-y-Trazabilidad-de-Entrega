package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_seguimiento")
@Getter @Setter @NoArgsConstructor
public class EventoSeguimiento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "envio_id", nullable = false) private Envio envio;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TipoEventoSeguimiento tipo;
    private String ubicacion;
    private String observacion;
    @Column(name = "visible_cliente", nullable = false) private boolean visibleCliente = true;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "registrado_por_id") private Usuario registradoPor;
    @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
}
