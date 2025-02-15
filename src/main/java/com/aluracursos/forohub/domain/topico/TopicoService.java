package com.aluracursos.forohub.domain.topico;

import com.aluracursos.forohub.domain.topico.dto.DatosActualizarTopico;
import com.aluracursos.forohub.domain.topico.dto.DatosRegistroTopico;
import com.aluracursos.forohub.domain.topico.dto.DatosRespuestaTopico;
import com.aluracursos.forohub.domain.topico.dto.DatosTopicoConRespuestas;
import com.aluracursos.forohub.domain.usuarios.UsuarioRepository;
import com.aluracursos.forohub.infra.security.ValidadorPermisos;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class TopicoService {
    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ValidadorPermisos validador;

    public Topico encontrarTopico(Long id){
        Optional<Topico> topicoBuscado = topicoRepository.findById(id);
        if (topicoBuscado.isPresent()){
            return topicoBuscado.get();
        } else {
            System.out.println("No se encuentra el id buscado.");
        }
        return null;
    }

    public boolean verificarCambiosTropico(Long id, DatosActualizarTopico datosNuevos){
        var topicoBuscado = encontrarTopico(id);
        return (datosNuevos.titulo() != null && !datosNuevos.titulo().equals(topicoBuscado.getTitulo()) )||
                (datosNuevos.mensaje() != null && !datosNuevos.mensaje().equals(topicoBuscado.getMensaje())) ||
                (datosNuevos.curso() != null && !datosNuevos.curso().equals(topicoBuscado.getCurso()));
    }

    @Transactional
    public ResponseEntity registrarTopico(@Valid @RequestBody DatosRegistroTopico datos,
                                          UriComponentsBuilder uriComponentsBuilder) {
        Long idUsuario = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (idUsuario == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no Autorizado");
        }
        var usuario = usuarioRepository.getReferenceById(idUsuario);
        Topico topico = topicoRepository.save(new Topico(datos, usuario, null));
        var detallesTopico = new DatosRespuestaTopico(topico);
        URI url = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(url).body(detallesTopico);
    }

    public Page<Topico> buscarTopicosPorTituloYFecha(@PageableDefault(sort = {"fechaDeCreacion"}) Pageable paginacion,
                                                     String titulo, LocalDate fecha) {
        if (fecha == null){
            var fechaDeInicio = topicoRepository.findByFechaDeCreacion();
            Page<Topico> topicos = topicoRepository.findByTituloAndFecha(paginacion, titulo, fechaDeInicio);
            return topicos;
        }
        throw new RuntimeException("Error en el metodo para obtener usuarios");
    }

    public ResponseEntity<DatosTopicoConRespuestas> obtenerTopidoPorId(@PathVariable Long id){
        Topico topico = topicoRepository.getReferenceById(id);
        if (!topico.getEstado().equals(Status.ELIMINADO)){
            var topicoDetallado = new DatosTopicoConRespuestas(topico);
            return ResponseEntity.ok(topicoDetallado);
        }
        return ResponseEntity.notFound().build();
    }

    @Transactional
    public ResponseEntity<DatosRespuestaTopico> actualizarTopico(Long id, @Valid DatosActualizarTopico datosNuevosTopico) {
        // Obtener el nombre del Usuario
        var topicoQueSeActualizara = encontrarTopico(id);
        if (validador.validarUsuarioactualEsAutor(topicoQueSeActualizara) || (validador.validarRolAdministrador() || validador.validarRolModerador())) {
            if (verificarCambiosTropico(id, datosNuevosTopico)) {
                topicoQueSeActualizara.actualizarDatos(datosNuevosTopico);
                return ResponseEntity.ok(new DatosRespuestaTopico(topicoQueSeActualizara));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @Transactional
    public ResponseEntity eliminarTopico(@PathVariable Long id){
        Topico topico = topicoRepository.getReferenceById(id);
        if (validador.validarUsuarioactualEsAutor(topico) || (validador.validarRolAdministrador() || validador.validarRolModerador())) {
            topico.desativarTopico();
            var respuestas = topico.getRespuestas();
            respuestas.stream().forEach(respuesta -> respuesta.desactivarRespuesta());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    public Page<Topico> buscarTopicosPersonales(@PageableDefault(sort = {"fechaDeCreacion"}) Pageable paginacion) {
        Long idUsuario = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var topicos = topicoRepository.findByEstadoAndId(idUsuario, paginacion);
        return topicos;
    }
}
