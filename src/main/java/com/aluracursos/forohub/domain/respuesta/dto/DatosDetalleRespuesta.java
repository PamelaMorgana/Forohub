package com.aluracursos.forohub.domain.respuesta.dto;

import com.aluracursos.forohub.domain.respuesta.Respuesta;

import java.time.LocalDateTime;

public record DatosDetalleRespuesta(
        Long id,
        String autor,
        Long idDelTopico,
        String tituloDelTopico,
        String mensaje,
        LocalDateTime fechaDeCreacion
) {
    public DatosDetalleRespuesta(Respuesta respuesta) {
        this(respuesta.getId(),
                respuesta.getUsuario().getNombre(), respuesta.getTopico().getId(),
                respuesta.getTopico().getTitulo(), respuesta.getMensaje(),
                respuesta.getFechaDeCreacion());
    }
}
