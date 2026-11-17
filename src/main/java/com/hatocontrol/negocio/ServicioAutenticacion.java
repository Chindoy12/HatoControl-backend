package com.hatocontrol.negocio;

import com.hatocontrol.presentacion.dto.IngresoRequest;
import com.hatocontrol.presentacion.dto.SesionResponse;
import org.springframework.stereotype.Service;

@Service
public class ServicioAutenticacion {

    public SesionResponse iniciarSesion(IngresoRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public void cerrarSesion() {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public void recuperarContrasena(String correo) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
