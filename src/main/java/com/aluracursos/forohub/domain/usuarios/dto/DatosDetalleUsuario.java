package com.aluracursos.forohub.domain.usuarios.dto;

import com.aluracursos.forohub.domain.usuarios.EstadoUsuario;
import com.aluracursos.forohub.domain.usuarios.Usuario;

public record DatosDetalleUsuario(
        Long id,
        String nombreDelUsuario,
        String email,
        EstadoUsuario estado
) {
    public DatosDetalleUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getEstado());
    }
}