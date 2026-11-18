package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioCuentas;
import com.hatocontrol.presentacion.dto.CuentaRequest;
import com.hatocontrol.presentacion.dto.IngresoRequest;
import com.hatocontrol.presentacion.dto.SesionResponse;
import com.hatocontrol.presentacion.dto.UsuarioResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuentas")
public class ControladorCuentas {

    private final ServicioCuentas servicioCuentas;
    private final FiltroAutenticacion filtro;

    public ControladorCuentas(ServicioCuentas servicioCuentas, FiltroAutenticacion filtro) {
        this.servicioCuentas = servicioCuentas;
        this.filtro = filtro;
    }

    /** Crea la finca y deja al ganadero dentro del sistema (HU_020). */
    @PostMapping("/fincas")
    @ResponseStatus(HttpStatus.CREATED)
    public SesionResponse registrarFinca(@Valid @RequestBody CuentaRequest solicitud, HttpServletRequest peticion) {
        return ControladorAutenticacion.abrirSesion(servicioCuentas.registrarFinca(solicitud), peticion);
    }

    @PostMapping("/administradores")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crearAdministrador(@Valid @RequestBody CuentaRequest solicitud, HttpServletRequest peticion) {
        return servicioCuentas.crearAdministrador(filtro.obtenerFinca(peticion), solicitud);
    }

    @GetMapping("/administradores")
    public List<UsuarioResponse> listarAdministradores(HttpServletRequest peticion) {
        return servicioCuentas.listarAdministradores(filtro.obtenerFinca(peticion));
    }

    /** Cuerpo: {"correo": "..."}. */
    @PostMapping("/administradores/desactivacion")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@RequestBody Map<String, String> datos, HttpServletRequest peticion) {
        servicioCuentas.desactivar(filtro.obtenerFinca(peticion), datos.get("correo"));
    }

    @PostMapping("/administradores/contrasena")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restablecerContrasena(@Valid @RequestBody IngresoRequest correoYContrasenaNueva, HttpServletRequest peticion) {
        servicioCuentas.restablecerContrasena(filtro.obtenerFinca(peticion), correoYContrasenaNueva.correo(), correoYContrasenaNueva.contrasena());
    }
}
