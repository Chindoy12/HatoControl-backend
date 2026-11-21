package com.hatocontrol.negocio.excepciones;

/** Lo que se busca no existe en la finca del usuario. */
public final class RecursoNoEncontradoException extends HatoControlException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
