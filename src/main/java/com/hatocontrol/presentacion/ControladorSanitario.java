package com.hatocontrol.presentacion;

import com.hatocontrol.modelo.TipoEventoSanitario;
import com.hatocontrol.negocio.ServicioSanitario;
import com.hatocontrol.presentacion.dto.EventoSanitarioRequest;
import com.hatocontrol.presentacion.dto.EventoSanitarioResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sanitario")
public class ControladorSanitario {

    private final ServicioSanitario servicioSanitario;

    public ControladorSanitario(ServicioSanitario servicioSanitario) {
        this.servicioSanitario = servicioSanitario;
    }

    @PostMapping("/animales/{codigoArete}/eventos")
    public EventoSanitarioResponse registrarEvento(@PathVariable String codigoArete, @RequestBody EventoSanitarioRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @PostMapping("/animales/{codigoArete}/programadas")
    public EventoSanitarioResponse programar(@PathVariable String codigoArete, @RequestBody EventoSanitarioRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @PostMapping("/eventos/{id}/realizada")
    public EventoSanitarioResponse marcarRealizada(@PathVariable Long id, @RequestParam LocalDate fechaRealizacion) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @GetMapping("/animales/{codigoArete}")
    public List<EventoSanitarioResponse> historial(@PathVariable String codigoArete, @RequestParam(required = false) TipoEventoSanitario tipo) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @GetMapping("/proximas")
    public List<EventoSanitarioResponse> proximas(@RequestParam int dias) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @GetMapping("/pendientes")
    public List<EventoSanitarioResponse> pendientes() {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
