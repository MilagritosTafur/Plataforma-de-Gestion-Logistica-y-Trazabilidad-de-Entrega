package com.example.demo.Repository;

import com.example.demo.Entity.Role;
import com.example.demo.Entity.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNombre(RolNombre nombre);
}
