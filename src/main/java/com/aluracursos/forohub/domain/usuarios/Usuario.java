package com.aluracursos.forohub.domain.usuarios;

import com.aluracursos.forohub.domain.respuesta.Respuesta;
import com.aluracursos.forohub.domain.topico.Topico;
import com.aluracursos.forohub.domain.usuarios.dto.DatosActualizarUsuario;
import com.aluracursos.forohub.domain.usuarios.dto.DatosCambioDeUsuario;
import com.aluracursos.forohub.domain.usuarios.dto.DatosRegistroUsuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(unique = true)
    private String email;
    private String clave;
    @Enumerated(EnumType.STRING)
    private PerfilUsuario perfil;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Topico> topicos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Respuesta> respuestas = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado;

    public Usuario(DatosRegistroUsuario datosUsuario, String claveEncriptada) {
        this.estado = EstadoUsuario.ACTIVO;
        this.nombre = datosUsuario.nombre();
        this.email = datosUsuario.email();
        this.clave = claveEncriptada;
        this.perfil = PerfilUsuario.USUARIO_REGISTRADO;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+this.perfil.name()));
    }

    @Override
    public String getPassword() {
        return clave;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void actualizarDatos(@Valid DatosActualizarUsuario datosActualizar, String clave) {
        if (datosActualizar.nombre() != null){
            this.nombre = datosActualizar.nombre();
        }
        if (datosActualizar.email() != null){
            this.email = datosActualizar.email();
        }
        if (datosActualizar.clave() != null){
            this.clave = clave;
        }
    }

    public void desactivarUsuario() {
        this.estado = EstadoUsuario.INACTIVO;
    }

    public void cambiarPerfil(DatosCambioDeUsuario datosCambioDeUsuario) {
        if (datosCambioDeUsuario.perfil().name() != null || datosCambioDeUsuario.perfil().name().equals(PerfilUsuario.MODERADOR)){
            this.perfil = PerfilUsuario.MODERADOR;
        }
        if (datosCambioDeUsuario.perfil() != null && datosCambioDeUsuario.perfil().equals(PerfilUsuario.ADMINISTRADOR)){
            this.perfil = PerfilUsuario.ADMINISTRADOR;
        }
        if (datosCambioDeUsuario.perfil() != null && datosCambioDeUsuario.perfil().equals(PerfilUsuario.USUARIO_REGISTRADO)){
            this.perfil = PerfilUsuario.USUARIO_REGISTRADO;
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public List<Topico> getTopicos() {
        return topicos;
    }

    public void setTopicos(List<Topico> topicos) {
        this.topicos = topicos;
    }

    public List<Respuesta> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }
}