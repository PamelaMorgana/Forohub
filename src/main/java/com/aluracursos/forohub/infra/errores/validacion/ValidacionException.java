package com.aluracursos.forohub.infra.errores.validacion;

public class ValidacionException extends RuntimeException{
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
