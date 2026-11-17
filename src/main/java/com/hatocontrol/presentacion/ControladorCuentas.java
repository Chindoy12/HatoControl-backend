package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioCuentas;
import com.hatocontrol.presentacion.dto.CuentaRequest;
import com.hatocontrol.presentacion.dto.IngresoRequest;
import com.hatocontrol.presentacion.dto.SesionResponse;
import com.hatocontrol.presentacion.dto.UsuarioResponse;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuentas")
public class ControladorCuentas {

    private final ServicioCuentas servicioCuentas;

    public ControladorCuentas(ServicioCuentas servicioCuentas) {
        this.servicioCuentas = servicioCuentas;
    }

    @PostMapping("/fincas")
    public SesionResponse registrarFinca(@RequestBody CuentaRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @PostMapping("/administradores")
    public UsuarioResponse crearAdministrador(@RequestBody CuentaRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @GetMapping("/administradores")
    public List<UsuarioResponse> listarAdministradores() {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @PostMapping("/administradores/desactivacion")
    public void desactivar(@RequestBody String correo) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @PostMapping("/administradores/contrasena")
    public void restablecerContrasena(@RequestBody IngresoRequest correoYContrasenaNueva) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
