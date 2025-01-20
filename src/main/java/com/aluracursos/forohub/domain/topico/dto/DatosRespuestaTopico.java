package com.aluracursos.forohub.domain.topico.dto;

import com.aluracursos.forohub.domain.curso.Curso;
import com.aluracursos.forohub.domain.topico.Status;
import com.aluracursos.forohub.domain.topico.Topico;

import java.time.LocalDateTime;

public record DatosRespuestaTopico(
        Long id,
        String autor,
        String titulo,
        String mensaje,
        LocalDateTime fechaDeCreacion,
        Status estado,
        Curso curso,
        Integer respuestas
) {
    public DatosRespuestaTopico (Topico topico){
        this(topico.getId(), topico.getUsuario().getNombre(),topico.getTitulo(),
                topico.getMensaje(), topico.getFechaDeCreacion(), topico.getEstado(),
                topico.getCurso(), topico.getRespuestas().size());
    }
}
