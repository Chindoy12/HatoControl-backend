package com.hatocontrol.presentacion;

import com.hatocontrol.modelo.Rol;
import com.hatocontrol.negocio.ServicioAutenticacion;
import com.hatocontrol.negocio.excepciones.AccesoDenegadoException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Comprueba la sesión en cada solicitud a /api y aplica los permisos por rol antes de llegar al
 * controlador (RNF19, RNF20). También comprueba que la cuenta siga activa. Una solicitud rechazada no cambia ningún dato.
 */
@Component
public class FiltroAutenticacion extends OncePerRequestFilter {

    static final List<String> RUTAS_PUBLICAS = List.of("/api/sesion/ingreso", "/api/sesion/recuperacion", "/api/cuentas/fincas");
    static final String USUARIO = "usuarioId";
    static final String FINCA = "fincaId";
    static final String ROL = "rol";
    static final String NOMBRE = "nombre";
    static final String NOMBRE_FINCA = "nombreFinca";

    private final ControlPermisos permisos;
    private final ServicioAutenticacion servicioAutenticacion;

    public FiltroAutenticacion(ControlPermisos permisos, ServicioAutenticacion servicioAutenticacion) {
        this.permisos = permisos;
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest solicitud, HttpServletResponse respuesta, FilterChain cadena)
            throws ServletException, IOException {
        // Carga el token CSRF para que la cookie XSRF-TOKEN llegue al navegador desde la primera respuesta.
        if (solicitud.getAttribute(CsrfToken.class.getName()) instanceof CsrfToken token) {
            token.getToken();
        }
        String ruta = solicitud.getRequestURI().substring(solicitud.getContextPath().length());
        if (!ruta.startsWith("/api/") || esRutaPublica(ruta)) {
            cadena.doFilter(solicitud, respuesta);
            return;
        }
        if (!validarSesion(solicitud)) {
            rechazar(respuesta, HttpServletResponse.SC_UNAUTHORIZED, "Inicia sesión para continuar.");
            return;
        }
        HttpSession sesion = solicitud.getSession(false);
        try {
            servicioAutenticacion.validarCuentaActiva((Long) sesion.getAttribute(USUARIO));
        } catch (AccesoDenegadoException e) {
            sesion.invalidate();
            rechazar(respuesta, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }
        try {
            permisos.verificar((Rol) sesion.getAttribute(ROL), funcionDe(ruta));
        } catch (AccesoDenegadoException e) {
            rechazar(respuesta, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }
        cadena.doFilter(solicitud, respuesta);
    }

    public boolean validarSesion(HttpServletRequest solicitud) {
        HttpSession sesion = solicitud.getSession(false);
        return sesion != null && sesion.getAttribute(FINCA) != null && sesion.getAttribute(ROL) != null;
    }

    /** La finca del usuario que inició sesión: toda consulta se limita a ella (HU_022). */
    public Long obtenerFinca(HttpServletRequest solicitud) {
        HttpSession sesion = solicitud.getSession(false);
        if (sesion == null || sesion.getAttribute(FINCA) == null) {
            throw new AccesoDenegadoException("Inicia sesión para continuar.");
        }
        return (Long) sesion.getAttribute(FINCA);
    }

    public boolean esRutaPublica(String ruta) {
        return RUTAS_PUBLICAS.stream().anyMatch(ruta::startsWith);
    }

    private static String funcionDe(String ruta) {
        if (ruta.startsWith("/api/reportes/dashboard")) {
            return ControlPermisos.DASHBOARD;
        }
        if (ruta.startsWith("/api/cuentas/administradores")) {
            return ControlPermisos.ADMINISTRADORES;
        }
        return ControlPermisos.GENERAL;
    }

    private static void rechazar(HttpServletResponse respuesta, int estado, String mensaje) throws IOException {
        respuesta.setStatus(estado);
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.setCharacterEncoding("UTF-8");
        respuesta.getWriter().write("{\"mensaje\":\"" + mensaje.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}");
    }
}
