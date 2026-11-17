package com.hatocontrol.negocio;

import com.hatocontrol.presentacion.dto.CuentaRequest;
import com.hatocontrol.presentacion.dto.SesionResponse;
import com.hatocontrol.presentacion.dto.UsuarioResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioCuentas {

    public SesionResponse registrarFinca(CuentaRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public UsuarioResponse crearAdministrador(Long fincaId, CuentaRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public List<UsuarioResponse> listarAdministradores(Long fincaId) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public void desactivar(Long fincaId, String correo) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public void restablecerContrasena(Long fincaId, String correo, String contrasenaNueva) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
