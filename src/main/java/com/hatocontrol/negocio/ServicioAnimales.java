package com.hatocontrol.negocio;

import com.hatocontrol.presentacion.dto.AnimalRequest;
import com.hatocontrol.presentacion.dto.AnimalResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioAnimales {

    public AnimalResponse registrar(Long fincaId, AnimalRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public List<AnimalResponse> listar(Long fincaId, String filtro) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public AnimalResponse actualizar(Long fincaId, String codigoArete, AnimalRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public void eliminar(Long fincaId, String codigoArete) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public AnimalResponse paginaCompleta(Long fincaId, String codigoArete) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public AnimalResponse pasarAEngorde(Long fincaId, String codigoArete) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
