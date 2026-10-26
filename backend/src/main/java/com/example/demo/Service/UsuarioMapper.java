package com.example.demo.Service;

import org.springframework.stereotype.Component;

import com.example.demo.Entity.Usuario;
import com.example.demo.DTO.UsuarioResponse;

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
