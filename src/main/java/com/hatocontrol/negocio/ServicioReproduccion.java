package com.hatocontrol.negocio;

import com.hatocontrol.presentacion.dto.PartoRequest;
import com.hatocontrol.presentacion.dto.PartoResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioReproduccion {

    public PartoResponse registrarParto(Long fincaId, PartoRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public List<PartoResponse> historialReproductivo(Long fincaId, String codigoArete) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
