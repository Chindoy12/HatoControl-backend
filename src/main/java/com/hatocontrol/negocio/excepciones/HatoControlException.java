package com.hatocontrol.negocio.excepciones;

/**
 * Error esperado del sistema, con un mensaje claro para el usuario (RNF03).
 * Es sellada: solo existen sus tres subclases, y ManejadorExcepciones las atiende todas en un solo lugar.
 */
public abstract sealed class HatoControlException extends RuntimeException
        permits ReglaNegocioException, RecursoNoEncontradoException, AccesoDenegadoException {

    protected HatoControlException(String mensaje) {
        super(mensaje);
    }
}
