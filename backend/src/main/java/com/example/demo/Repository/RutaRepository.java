package com.example.demo.Repository;

import com.example.demo.Entity.EstadoRuta;
import com.example.demo.Entity.Ruta;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutaRepository extends JpaRepository<Ruta, Long> {
    boolean existsByRepartidorIdAndEstadoIn(Long repartidorId, Collection<EstadoRuta> estados);
    boolean existsByVehiculoIdAndEstadoIn(Long vehiculoId, Collection<EstadoRuta> estados);
    List<Ruta> findByRepartidorUsuarioEmailIgnoreCaseOrderByFechaProgramadaDesc(String email);
}
