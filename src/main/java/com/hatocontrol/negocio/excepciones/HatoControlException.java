package com.hatocontrol.negocio.excepciones;

public abstract sealed class HatoControlException extends RuntimeException
        permits ReglaNegocioException, RecursoNoEncontradoException, AccesoDenegadoException {

    protected HatoControlException(String mensaje) {
        super(mensaje);
    }
}
