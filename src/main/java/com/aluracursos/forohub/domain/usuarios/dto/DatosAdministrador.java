package com.aluracursos.forohub.domain.usuarios.dto;

import com.aluracursos.forohub.domain.usuarios.Usuario;

public record DatosAdministrador(
        Long id,
        String nombreDelUsuario,
        String email,
        String perfil,
        Integer cantidadDeTopicos,
        Integer cantidadDeRespuestas
) {
    public DatosAdministrador(Usuario usuario) {
        this(usuario.getId(), usuario.getNombre(), usuario.getEmail()
                , usuario.getPerfil().name(), usuario.getTopicos().size(),
                usuario.getRespuestas().size());
    }
}
