package com.hatocontrol.negocio;

import com.hatocontrol.presentacion.dto.PesoRequest;
import com.hatocontrol.presentacion.dto.PesoResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioPesos {

    public PesoResponse registrar(Long fincaId, String codigoArete, PesoRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public List<PesoResponse> historialConDiferencias(Long fincaId, String codigoArete) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
