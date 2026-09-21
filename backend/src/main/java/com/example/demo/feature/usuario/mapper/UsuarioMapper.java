package com.example.demo.feature.usuario.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.domain.model.Usuario;
import com.example.demo.feature.usuario.dto.UsuarioResponse;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getTelefono(),
                usuario.getRol().getNombre(),
                usuario.isActivo()
        );
    }
}
