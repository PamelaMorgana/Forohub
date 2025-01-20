package com.aluracursos.forohub.domain.usuarios;

import com.aluracursos.forohub.domain.usuarios.dto.*;
import com.aluracursos.forohub.infra.errores.validacion.ValidacionException;
import com.aluracursos.forohub.infra.security.ValidadorPermisos;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ValidadorPermisos validadorPermisos;

    @Transactional
    public ResponseEntity registrarUsuario(@Valid @RequestBody DatosRegistroUsuario datosUsuario,
                                           UriComponentsBuilder uriComponentsBuilder){
        String claveEncriptada = passwordEncoder.encode(datosUsuario.clave());
        Usuario usuario = usuarioRepository.save(new Usuario(datosUsuario, claveEncriptada));
        var datosDeUsuarioRegistrado = new DatosDetalleUsuario(usuario);
        URI url = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(url).body(datosDeUsuarioRegistrado);
    }

    public DatosDetalleUsuario obtenerUsuarioEspecifico(@PathVariable Long id){
        Usuario usuario = usuarioRepository.getReferenceById(id);
        return new DatosDetalleUsuario(usuario);
    }

    // Lista usuarios
    public ResponseEntity obtenerListadoUsuarios(@PageableDefault(size = 10) Pageable paginacion){
        var usuarios = usuarioRepository.findAll(paginacion);
        return ResponseEntity.ok(usuarios.map(DatosDetalleUsuario::new));
    }

    public Usuario obtenerElIdUsuarioActual(){
        Long idUsuario = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (idUsuario == null){
            return null;
        }
        Usuario usuario = usuarioRepository.getReferenceById(idUsuario);
        return usuario;
    }

    public boolean verificarCambiosUsuario(Long id, DatosActualizarUsuario datosNuevos){
        var usuario = usuarioRepository.getReferenceById(id);
        return (datosNuevos.nombre() != null && !datosNuevos.nombre().equals(usuario.getNombre())) ||
                (datosNuevos.email() != null && !datosNuevos.email().equals(usuario.getEmail())) ||
                ((datosNuevos.clave() != null && !datosNuevos.clave().equals(usuario.getClave())));
    }

    // Actualizar Usuarios
    @Transactional
    public DatosDetalleUsuario actualizarInformacionDelUsuario(@RequestBody @Valid DatosActualizarUsuario datosActualizar){
        Usuario usuario = obtenerElIdUsuarioActual();
        if (validadorPermisos.validarPerfilDeUsuario(PerfilUsuario.ADMINISTRADOR)){
            if (verificarCambiosUsuario(usuario.getId(), datosActualizar)){
                var claveEncriptada = passwordEncoder.encode(datosActualizar.clave());
                usuario.actualizarDatos(datosActualizar, claveEncriptada);
                return new DatosDetalleUsuario(usuario);
            }
            throw new ValidacionException("Los datos ingresados no presentan cambios.");
        }

        throw new ValidacionException("El usuario no tiene permiso para esta acción.");
    }

    @Transactional
    public ResponseEntity eliminarUsuario(@PathVariable Long id){
        if (validadorPermisos.validarPerfilDeUsuario(PerfilUsuario.ADMINISTRADOR)){
            Usuario usuario = usuarioRepository.getReferenceById(id);
            usuario.desactivarUsuario();
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Transactional
    public DatosAdministrador cambiarPerfilUsuario(Long id, DatosCambioDeUsuario datosCambioDeUsuario) {
        if (validadorPermisos.validarPerfilDeUsuario(PerfilUsuario.ADMINISTRADOR)) {
            Usuario usuario = usuarioRepository.getReferenceById(id);
            usuario.cambiarPerfil(datosCambioDeUsuario);
            return new DatosAdministrador(usuario);
        } else {
            throw new ValidacionException("El usuario no tiene permiso para realizar esta acción");
        }
    }
}
