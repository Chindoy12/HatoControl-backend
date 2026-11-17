package com.hatocontrol.negocio;

import org.springframework.stereotype.Component;

/** Envía el correo de recuperación por Amazon SES (Spring Mail). */
@Component
public class AdaptadorCorreo implements IServicioCorreo {

    @Override
    public void enviarEnlace(String correo, String enlace) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
