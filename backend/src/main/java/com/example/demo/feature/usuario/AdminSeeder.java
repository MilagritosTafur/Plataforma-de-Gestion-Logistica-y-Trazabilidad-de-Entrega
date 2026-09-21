package com.example.demo.feature.usuario;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.domain.model.RolNombre;
import com.example.demo.domain.model.Role;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.repository.RoleRepository;
import com.example.demo.domain.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default-email}")
    private String adminEmail;

    @Value("${admin.default-password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByRolNombre(RolNombre.ADMINISTRADOR)) {
            return;
        }

        Role rolAdministrador = roleRepository.findByNombre(RolNombre.ADMINISTRADOR)
                .orElseThrow(() -> new IllegalStateException("El rol ADMINISTRADOR no existe. Verifica las migraciones de Flyway."));

        Usuario admin = new Usuario();
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setNombre("Administrador");
        admin.setApellido("Sistema");
        admin.setRol(rolAdministrador);
        admin.setActivo(true);
        admin.setCreadoPor(null);

        usuarioRepository.save(admin);

        log.warn("Cuenta ADMINISTRADOR creada automaticamente: {} — cambia la contrasena por defecto de inmediato.", adminEmail);
    }
}
