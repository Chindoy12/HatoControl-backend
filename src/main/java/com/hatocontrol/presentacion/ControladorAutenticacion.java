package com.hatocontrol.presentacion;

import com.hatocontrol.modelo.Rol;
import com.hatocontrol.modelo.Usuario;
import com.hatocontrol.negocio.ServicioAutenticacion;
import com.hatocontrol.presentacion.dto.IngresoRequest;
import com.hatocontrol.presentacion.dto.SesionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sesion")
public class ControladorAutenticacion {

    static final String MENSAJE_RECUPERACION =
            "Si el correo es de una cuenta de ganadero, te enviamos un enlace para crear una contraseña nueva. Vence en 30 minutos.";

    private final ServicioAutenticacion servicioAutenticacion;

    public ControladorAutenticacion(ServicioAutenticacion servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @GetMapping
    public SesionResponse sesionActual(HttpServletRequest solicitud) {
        HttpSession sesion = solicitud.getSession(false);
        return new SesionResponse((String) sesion.getAttribute(FiltroAutenticacion.NOMBRE), (Rol) sesion.getAttribute(FiltroAutenticacion.ROL),
                (String) sesion.getAttribute(FiltroAutenticacion.NOMBRE_FINCA));
    }

    @PostMapping("/ingreso")
    public SesionResponse ingresar(@Valid @RequestBody IngresoRequest solicitud, HttpServletRequest peticion) {
        return abrirSesion(servicioAutenticacion.iniciarSesion(solicitud), peticion);
    }

    @PostMapping("/salida")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void salir(HttpServletRequest solicitud) {
        servicioAutenticacion.cerrarSesion();
        HttpSession sesion = solicitud.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
    }

    @PostMapping("/recuperacion")
    public Map<String, String> solicitarRecuperacion(@RequestBody Map<String, String> datos) {
        servicioAutenticacion.recuperarContrasena(datos.get("correo"));
        return Map.of("mensaje", MENSAJE_RECUPERACION);
    }

    @PostMapping("/recuperacion/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restablecerConEnlace(@PathVariable String token, @RequestBody Map<String, String> datos) {
        servicioAutenticacion.restablecerConEnlace(token, datos.get("contrasena"));
    }

    static SesionResponse abrirSesion(Usuario usuario, HttpServletRequest peticion) {
        HttpSession anterior = peticion.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }
        HttpSession sesion = peticion.getSession(true);
        sesion.setAttribute(FiltroAutenticacion.USUARIO, usuario.getId());
        sesion.setAttribute(FiltroAutenticacion.FINCA, usuario.getFinca().getId());
        sesion.setAttribute(FiltroAutenticacion.ROL, usuario.getRol());
        sesion.setAttribute(FiltroAutenticacion.NOMBRE, usuario.getNombre());
        sesion.setAttribute(FiltroAutenticacion.NOMBRE_FINCA, usuario.getFinca().getNombre());
        return new SesionResponse(usuario.getNombre(), usuario.getRol(), usuario.getFinca().getNombre());
    }
}
