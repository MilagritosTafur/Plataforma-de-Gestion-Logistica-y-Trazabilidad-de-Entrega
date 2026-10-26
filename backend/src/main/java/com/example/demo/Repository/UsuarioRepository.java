package com.example.demo.Repository;

import com.example.demo.Entity.RolNombre;
import com.example.demo.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Collection;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByRolNombre(RolNombre nombre);

    List<Usuario> findByRolNombreInOrderByNombreAsc(Collection<RolNombre> roles);
}
