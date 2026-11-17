package com.hatocontrol.negocio;

import com.hatocontrol.modelo.TipoEventoSanitario;
import com.hatocontrol.presentacion.dto.EventoSanitarioRequest;
import com.hatocontrol.presentacion.dto.EventoSanitarioResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioSanitario {

    public EventoSanitarioResponse registrarEvento(Long fincaId, String codigoArete, EventoSanitarioRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public EventoSanitarioResponse programar(Long fincaId, String codigoArete, EventoSanitarioRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public EventoSanitarioResponse marcarRealizada(Long fincaId, Long eventoId, LocalDate fechaRealizacion) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public List<EventoSanitarioResponse> historial(Long fincaId, String codigoArete, TipoEventoSanitario tipo) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public List<EventoSanitarioResponse> proximas(Long fincaId, int dias) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    public List<EventoSanitarioResponse> pendientes(Long fincaId) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
