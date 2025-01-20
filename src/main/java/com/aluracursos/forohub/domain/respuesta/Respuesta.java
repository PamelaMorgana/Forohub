package com.aluracursos.forohub.domain.respuesta;

import com.aluracursos.forohub.domain.respuesta.dto.DatosActualizarRespuesta;
import com.aluracursos.forohub.domain.respuesta.dto.DatosRegistroRespuesta;
import com.aluracursos.forohub.domain.topico.ComentTopico;
import com.aluracursos.forohub.domain.topico.Topico;
import com.aluracursos.forohub.domain.usuarios.Usuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "respuestas", uniqueConstraints = @UniqueConstraint(columnNames = "mensaje"))
@Entity(name = "Respuesta")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Respuesta extends ComentTopico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String mensaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topico_id")
    private Topico topico;

    private LocalDateTime fechaDeCreacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private boolean solucion;
    @Enumerated(EnumType.STRING)
    private StatusRespuesta estado;

    public Respuesta(@Valid DatosRegistroRespuesta datos, Usuario usuario, Topico topico) {
        this.estado = StatusRespuesta.ACTIVO;
        this.mensaje = datos.mensaje();
        this.fechaDeCreacion = LocalDateTime.now();
        this.usuario = usuario;
        this.topico = topico;
        this.solucion = false;
    }


    public void actualizarRespuesta(@Valid DatosActualizarRespuesta datosNuevos) {
        if (datosNuevos.mensaje() != null){
            this.estado = StatusRespuesta.ACTUALIZADO;
            this.mensaje = datosNuevos.mensaje();
        }

    }

    public void desactivarRespuesta() {
        this.estado = StatusRespuesta.ELIMINADO;
    }

    public void solucionarRespuesta() {
        this.solucion = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Topico getTopico() {
        return topico;
    }

    public void setTopico(Topico topico) {
        this.topico = topico;
    }

    public LocalDateTime getFechaDeCreacion() {
        return fechaDeCreacion;
    }

    public void setFechaDeCreacion(LocalDateTime fechaDeCreacion) {
        this.fechaDeCreacion = fechaDeCreacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isSolucion() {
        return solucion;
    }

    public void setSolucion(boolean solucion) {
        this.solucion = solucion;
    }

    public StatusRespuesta getEstado() {
        return estado;
    }

    public void setEstado(StatusRespuesta estado) {
        this.estado = estado;
    }
}