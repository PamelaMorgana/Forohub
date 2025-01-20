package com.aluracursos.forohub.domain.usuarios.dto;

import com.aluracursos.forohub.domain.usuarios.PerfilUsuario;
import jakarta.validation.constraints.NotNull;

public record DatosCambioDeUsuario(
        @NotNull
        PerfilUsuario perfil
) {
}
