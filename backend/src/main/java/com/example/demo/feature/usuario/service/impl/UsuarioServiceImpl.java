package com.example.demo.feature.usuario.service.impl;

import java.util.Set;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.domain.model.RolNombre;
import com.example.demo.domain.model.Role;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.model.Repartidor;
import com.example.demo.domain.repository.RoleRepository;
import com.example.demo.domain.repository.UsuarioRepository;
import com.example.demo.domain.repository.RepartidorRepository;
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
    private final RepartidorRepository repartidorRepository;

    @Override
    @Transactional
    public UsuarioResponse crearCuenta(CrearCuentaRequest request, String emailAdminCreador) {
        if (!ROLES_ASIGNABLES_POR_ADMIN.contains(request.rol())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El administrador solo puede crear cuentas con rol OPERADOR o REPARTIDOR");
        }

        String email = normalizarEmail(request.email());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya esta registrado");
        }

        String documento = null;
        String licencia = null;
        if (request.rol() == RolNombre.REPARTIDOR) {
            documento = normalizarIdentificador(request.documento());
            licencia = normalizarIdentificador(request.licenciaConducir());
            if (documento.isBlank() || licencia.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Para crear un repartidor se requiere documento y licencia de conducir");
            }
            if (repartidorRepository.existsByDocumentoIgnoreCase(documento)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El documento del repartidor ya esta registrado");
            }
            if (repartidorRepository.existsByLicenciaConducirIgnoreCase(licencia)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "La licencia de conducir ya esta registrada");
            }
        }

        Usuario admin = usuarioRepository.findByEmailIgnoreCase(normalizarEmail(emailAdminCreador))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion invalida"));

        Role rol = roleRepository.findByNombre(request.rol())
                .orElseThrow(() -> new IllegalStateException("El rol " + request.rol() + " no existe. Verifica las migraciones de Flyway."));

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setTelefono(request.telefono());
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setCreadoPor(admin);

        usuarioRepository.save(usuario);

        if (request.rol() == RolNombre.REPARTIDOR) {
            Repartidor repartidor = new Repartidor();
            repartidor.setUsuario(usuario);
            repartidor.setDocumento(documento);
            repartidor.setLicenciaConducir(licencia);
            repartidor.setDisponible(true);
            repartidorRepository.save(repartidor);
        }

        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarEquipo() {
        return usuarioRepository.findByRolNombreInOrderByNombreAsc(ROLES_ASIGNABLES_POR_ADMIN)
                .stream().map(usuarioMapper::toResponse).toList();
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarIdentificador(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase(Locale.ROOT);
    }
}
