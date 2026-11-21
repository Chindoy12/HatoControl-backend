package com.hatocontrol.negocio.excepciones;

/** Una regla de negocio no se cumple (dato repetido, fecha no permitida, propósito que no corresponde…). */
public final class ReglaNegocioException extends HatoControlException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
