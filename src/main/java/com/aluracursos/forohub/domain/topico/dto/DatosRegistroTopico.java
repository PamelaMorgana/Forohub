package com.aluracursos.forohub.domain.topico.dto;

import jakarta.validation.constraints.NotNull;

public record DatosRegistroTopico(
        String idUsuario,
        @NotNull
        String mensaje,
        @NotNull
        String nombreDelCurso,
        @NotNull
        String titulo
) {

}
