package com.aluracursos.forohub.domain.respuesta.dto;

import jakarta.validation.constraints.NotNull;

public record DatosRegistroRespuesta(
        @NotNull
        String idTopico,
        @NotNull
        String mensaje
) {
}
