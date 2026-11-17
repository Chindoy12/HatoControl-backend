package com.hatocontrol.presentacion;

import com.hatocontrol.modelo.Rol;
import org.springframework.stereotype.Component;

/** Verifica que el rol del usuario pueda usar la función pedida (RNF20). */
@Component
public class ControlPermisos {

    public void verificar(Rol rol, String funcion) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
