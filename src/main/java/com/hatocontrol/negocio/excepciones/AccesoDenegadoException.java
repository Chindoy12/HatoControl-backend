package com.hatocontrol.negocio.excepciones;

/** El usuario no tiene permiso, la sesión no existe o la cuenta está desactivada. */
public final class AccesoDenegadoException extends HatoControlException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
