package com.aluracursos.forohub.domain.respuesta;

import com.aluracursos.forohub.domain.respuesta.dto.DatosActualizarRespuesta;
import com.aluracursos.forohub.domain.respuesta.dto.DatosDetalleRespuesta;
import com.aluracursos.forohub.domain.respuesta.dto.DatosRegistroRespuesta;
import com.aluracursos.forohub.domain.topico.TopicoRepository;
import com.aluracursos.forohub.domain.usuarios.Usuario;
import com.aluracursos.forohub.domain.usuarios.UsuarioRepository;
import com.aluracursos.forohub.infra.errores.validacion.ValidacionException;
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
import java.util.Optional;

@Service
public class RespuestaService {
    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private ValidadorPermisos validador;

    // Encontrar Respuesta por ID
    public Respuesta validarRespuesta(Long id){
        Optional<Respuesta> respuestaABuscar =respuestaRepository.findById(id);
        Respuesta respuesta = respuestaABuscar.get();
        if (respuestaABuscar.isPresent()){
            respuestaABuscar.get();
        } else {
            System.out.println("No hay una respuesta registrada con este id.");
        }
        return respuesta;
    }

    public boolean verificarCambiosDeRespuesta(Long id, DatosActualizarRespuesta datosActualizarRespuesta){
        var topicoBuscado = validarRespuesta(id);
        return !datosActualizarRespuesta.mensaje().equals(topicoBuscado.getMensaje());
    }

    @Transactional
    public ResponseEntity responderTopico(@Valid @RequestBody DatosRegistroRespuesta datos,
                                          UriComponentsBuilder uriComponentsBuilder){
        Long idUsuario = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (idUsuario == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no Autorizado");
        }
        Usuario usuario = usuarioRepository.getReferenceById(idUsuario);
        var topico = topicoRepository.getReferenceById(Long.valueOf(datos.idTopico()));

        if (topico == null){
            throw new ValidacionException("El topico que esta ingresando no es valido");
        }
        Respuesta respuesta = respuestaRepository.save(new Respuesta(datos, usuario, topico));
        var detalleRespuesta = new DatosDetalleRespuesta(respuesta);
        URI url = uriComponentsBuilder.path("/respuestas/{id}").buildAndExpand( respuesta.getId()).toUri();
        return ResponseEntity.created(url).body(detalleRespuesta);

    }

    public ResponseEntity<Page<DatosDetalleRespuesta>> obtenerListadoDeRespuestas(
            @PageableDefault(sort = {"fechaDeCreacion"}) Pageable pageable){
        var respuestas = respuestaRepository.findByEstado(pageable);
        return ResponseEntity.ok(respuestas.map(DatosDetalleRespuesta::new));
    }

    public ResponseEntity<DatosDetalleRespuesta> obtenerComentarioEspecifico(@PathVariable Long id){
        Respuesta respuesta = respuestaRepository.getReferenceById(id);
        if (respuesta.getEstado().equals(StatusRespuesta.ELIMINADO)){
            return ResponseEntity.ok(new DatosDetalleRespuesta(respuesta));
        }
        return ResponseEntity.notFound().build();
    }

    @Transactional
    public ResponseEntity<DatosDetalleRespuesta> actualizarRespuesta(@Valid DatosActualizarRespuesta datos,
                                                                     @PathVariable Long id){
        Respuesta respuestaAActualizar = validarRespuesta(id);
        if (validador.validarUsuarioactualEsAutor(respuestaAActualizar) ||
                (validador.validarRolAdministrador() || validador.validarRolModerador())){
            if (verificarCambiosDeRespuesta(id, datos)){
                respuestaAActualizar.actualizarRespuesta(datos);
                return ResponseEntity.ok(new DatosDetalleRespuesta(respuestaAActualizar));
            }
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Transactional
    public ResponseEntity eliminarRespuesta(@PathVariable Long id){
        Respuesta respuesta = respuestaRepository.getReferenceById(id);
        if (validador.validarUsuarioactualEsAutor(respuesta) ||
                (validador.validarRolAdministrador() || validador.validarRolModerador())){
            Respuesta respuestaAEliminar = validarRespuesta(id);
            respuestaAEliminar.desactivarRespuesta();
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Transactional
    public DatosDetalleRespuesta solucionarTopico(Long id){
        Respuesta respuesta = respuestaRepository.getReferenceById(id);
        if (respuesta == null){
            throw new ValidacionException("Registro no encontrado.");
        }
        if (validador.validarRolAdministrador() || validador.validarRolModerador()){
            respuesta.solucionarRespuesta();
            return new DatosDetalleRespuesta(respuesta);
        } else {
            throw new ValidacionException("El usuario no tiene permiso para esta acción.");
        }
    }

}
