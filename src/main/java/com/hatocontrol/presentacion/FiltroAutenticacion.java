package com.hatocontrol.presentacion;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Comprueba la sesión en cada solicitud a /api (RNF19, RNF20). */
@Component
public class FiltroAutenticacion extends OncePerRequestFilter {

    static final List<String> RUTAS_PUBLICAS = List.of("/api/sesion/ingreso", "/api/sesion/recuperacion", "/api/cuentas/fincas");

    @Override
    protected void doFilterInternal(HttpServletRequest solicitud, HttpServletResponse respuesta, FilterChain cadena)
            throws ServletException, IOException {
        // La validación de la sesión se completa en la Fase 9.
        cadena.doFilter(solicitud, respuesta);
    }

    public boolean validarSesion(HttpServletRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public Long obtenerFinca(HttpServletRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public boolean esRutaPublica(String ruta) {
        return RUTAS_PUBLICAS.stream().anyMatch(ruta::startsWith);
    }
}
