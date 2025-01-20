package com.aluracursos.forohub.domain.topico.dto;

import com.aluracursos.forohub.domain.curso.Curso;
import com.fasterxml.jackson.annotation.JsonAlias;

public record DatosActualizarTopico(
        String titulo,
        String mensaje,
        Curso curso
) {
}
