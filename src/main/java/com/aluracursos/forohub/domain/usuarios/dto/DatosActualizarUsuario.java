package com.aluracursos.forohub.domain.usuarios.dto;

import jakarta.validation.constraints.Email;

public record DatosActualizarUsuario(
        String nombre,
        @Email
        String email,
        String clave
) {
}
