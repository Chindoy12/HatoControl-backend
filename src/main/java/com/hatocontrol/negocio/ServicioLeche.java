package com.hatocontrol.negocio;

import com.hatocontrol.presentacion.dto.LecheRequest;
import com.hatocontrol.presentacion.dto.LecheResponse;
import com.hatocontrol.presentacion.dto.PromedioLecheResponse;
import org.springframework.stereotype.Service;

@Service
public class ServicioLeche {

    public LecheResponse registrar(Long fincaId, LecheRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public PromedioLecheResponse promedio(Long fincaId, String codigoArete, String periodo) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
