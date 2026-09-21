package com.example.demo.feature.usuario.service.impl;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.domain.model.RolNombre;
import com.example.demo.domain.model.Role;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.repository.RoleRepository;
import com.example.demo.domain.repository.UsuarioRepository;
import com.example.demo.feature.usuario.dto.CrearCuentaRequest;
import com.example.demo.feature.usuario.dto.UsuarioResponse;
import com.example.demo.feature.usuario.mapper.UsuarioMapper;
import com.example.demo.feature.usuario.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private static final Set<RolNombre> ROLES_ASIGNABLES_POR_ADMIN = Set.of(RolNombre.OPERADOR, RolNombre.REPARTIDOR);

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional
    public UsuarioResponse crearCuenta(CrearCuentaRequest request, String emailAdminCreador) {
        if (!ROLES_ASIGNABLES_POR_ADMIN.contains(request.rol())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El administrador solo puede crear cuentas con rol OPERADOR o REPARTIDOR");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya esta registrado");
        }

        Usuario admin = usuarioRepository.findByEmail(emailAdminCreador)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion invalida"));

        Role rol = roleRepository.findByNombre(request.rol())
                .orElseThrow(() -> new IllegalStateException("El rol " + request.rol() + " no existe. Verifica las migraciones de Flyway."));

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setTelefono(request.telefono());
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setCreadoPor(admin);

        usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuario);
    }
}
