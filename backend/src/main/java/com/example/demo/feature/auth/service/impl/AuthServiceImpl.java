package com.example.demo.feature.auth.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.domain.model.RolNombre;
import com.example.demo.domain.model.Role;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.model.Cliente;
import com.example.demo.domain.repository.ClienteRepository;
import com.example.demo.domain.repository.RoleRepository;
import com.example.demo.domain.repository.UsuarioRepository;
import com.example.demo.feature.auth.dto.LoginRequest;
import com.example.demo.feature.auth.dto.LoginResponse;
import com.example.demo.feature.auth.dto.RegistroRequest;
import com.example.demo.feature.auth.service.AuthService;
import com.example.demo.feature.usuario.dto.UsuarioResponse;
import com.example.demo.feature.usuario.mapper.UsuarioMapper;
import com.example.demo.security.JwtService;

import lombok.RequiredArgsConstructor;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioMapper usuarioMapper;
    private final ClienteRepository clienteRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(normalizarEmail(request.email()))
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        if (!usuario.isActivo()) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        String token = jwtService.generateToken(usuario);

        return new LoginResponse(token, "Bearer", jwtService.getExpirationMs(), usuarioMapper.toResponse(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponse registrarUsuario(RegistroRequest request) {
        String email = normalizarEmail(request.email());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya esta registrado");
        }

        Role rolUsuario = roleRepository.findByNombre(RolNombre.USUARIO)
                .orElseThrow(() -> new IllegalStateException("El rol USUARIO no existe. Verifica las migraciones de Flyway."));

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setTelefono(request.telefono());
        usuario.setRol(rolUsuario);
        usuario.setActivo(true);
        usuario.setCreadoPor(null);

        usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNombres(usuario.getNombre());
        cliente.setApellidos(usuario.getApellido());
        cliente.setEmail(usuario.getEmail());
        cliente.setTelefono(usuario.getTelefono());
        cliente.setActivo(true);
        clienteRepository.save(cliente);

        return usuarioMapper.toResponse(usuario);
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
