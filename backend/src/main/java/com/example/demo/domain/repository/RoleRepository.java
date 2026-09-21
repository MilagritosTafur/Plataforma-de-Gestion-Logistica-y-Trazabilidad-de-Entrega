package com.example.demo.domain.repository;

import com.example.demo.domain.model.Role;
import com.example.demo.domain.model.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNombre(RolNombre nombre);
}
