package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioAutenticacion;
import com.hatocontrol.presentacion.dto.IngresoRequest;
import com.hatocontrol.presentacion.dto.SesionResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sesion")
public class ControladorAutenticacion {

    private final ServicioAutenticacion servicioAutenticacion;

    public ControladorAutenticacion(ServicioAutenticacion servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @PostMapping("/ingreso")
    public SesionResponse ingresar(@RequestBody IngresoRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @PostMapping("/salida")
    public void salir() {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @PostMapping("/recuperacion")
    public void solicitarRecuperacion(@RequestBody String correo) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @PostMapping("/recuperacion/{token}")
    public void restablecerConEnlace(@PathVariable String token, @RequestBody String contrasenaNueva) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
