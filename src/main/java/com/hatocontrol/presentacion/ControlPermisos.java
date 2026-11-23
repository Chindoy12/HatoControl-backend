package com.hatocontrol.presentacion;

import com.hatocontrol.modelo.Rol;
import com.hatocontrol.negocio.excepciones.AccesoDenegadoException;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ControlPermisos {

    static final String DASHBOARD = "dashboard";
    static final String ADMINISTRADORES = "administradores";
    static final String GENERAL = "general";

    private static final Set<String> SOLO_GANADERO = Set.of(DASHBOARD, ADMINISTRADORES);

    public void verificar(Rol rol, String funcion) {
        if (rol == null) {
            throw new AccesoDenegadoException("Inicia sesión para continuar.");
        }
        if (SOLO_GANADERO.contains(funcion) && rol != Rol.GANADERO) {
            throw new AccesoDenegadoException("Esta opción es solo del ganadero. No se cambió ningún dato.");
        }
    }
}
