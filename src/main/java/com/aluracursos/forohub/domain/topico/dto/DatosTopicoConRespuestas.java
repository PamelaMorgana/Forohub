package com.aluracursos.forohub.domain.topico.dto;

import com.aluracursos.forohub.domain.curso.Curso;
import com.aluracursos.forohub.domain.respuesta.dto.DatosDetalleRespuesta;
import com.aluracursos.forohub.domain.topico.Status;
import com.aluracursos.forohub.domain.topico.Topico;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record DatosTopicoConRespuestas(
        Long id,
        String autor,
        String titulo,
        String mensaje,
        LocalDateTime fechaDeCreacion,
        Status estado,
        Curso curso,
        List<DatosDetalleRespuesta> respuestas
) {
        public DatosTopicoConRespuestas (Topico topico){
                this(topico.getId(), topico.getUsuario().getNombre(),topico.getTitulo(),
                        topico.getMensaje(), topico.getFechaDeCreacion(), topico.getEstado(),
                        topico.getCurso(), topico.getRespuestas()
                                .stream()
                                .map(respuesta -> new DatosDetalleRespuesta(respuesta))
                                .collect(Collectors.toList()));
        }
}
