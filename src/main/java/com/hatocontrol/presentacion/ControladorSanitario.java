package com.hatocontrol.presentacion;

import com.hatocontrol.modelo.TipoEventoSanitario;
import com.hatocontrol.negocio.ServicioSanitario;
import com.hatocontrol.presentacion.dto.EventoSanitarioRequest;
import com.hatocontrol.presentacion.dto.EventoSanitarioResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sanitario")
public class ControladorSanitario {

    private final ServicioSanitario servicioSanitario;
    private final FiltroAutenticacion filtro;

    public ControladorSanitario(ServicioSanitario servicioSanitario, FiltroAutenticacion filtro) {
        this.servicioSanitario = servicioSanitario;
        this.filtro = filtro;
    }

    @PostMapping("/animales/{codigoArete}/eventos")
    @ResponseStatus(HttpStatus.CREATED)
    public EventoSanitarioResponse registrarEvento(@PathVariable String codigoArete, @Valid @RequestBody EventoSanitarioRequest solicitud,
            HttpServletRequest peticion) {
        return servicioSanitario.registrarEvento(filtro.obtenerFinca(peticion), codigoArete, solicitud);
    }

    @PostMapping("/animales/{codigoArete}/programadas")
    @ResponseStatus(HttpStatus.CREATED)
    public EventoSanitarioResponse programar(@PathVariable String codigoArete, @Valid @RequestBody EventoSanitarioRequest solicitud,
            HttpServletRequest peticion) {
        return servicioSanitario.programar(filtro.obtenerFinca(peticion), codigoArete, solicitud);
    }

    @PostMapping("/eventos/{id}/realizada")
    public EventoSanitarioResponse marcarRealizada(@PathVariable Long id, @RequestParam LocalDate fechaRealizacion, HttpServletRequest peticion) {
        return servicioSanitario.marcarRealizada(filtro.obtenerFinca(peticion), id, fechaRealizacion);
    }

    @GetMapping("/animales/{codigoArete}")
    public List<EventoSanitarioResponse> historial(@PathVariable String codigoArete, @RequestParam(required = false) TipoEventoSanitario tipo,
            HttpServletRequest peticion) {
        return servicioSanitario.historial(filtro.obtenerFinca(peticion), codigoArete, tipo);
    }

    @GetMapping("/proximas")
    public List<EventoSanitarioResponse> proximas(@RequestParam int dias, HttpServletRequest peticion) {
        return servicioSanitario.proximas(filtro.obtenerFinca(peticion), dias);
    }

    @GetMapping("/pendientes")
    public List<EventoSanitarioResponse> pendientes(HttpServletRequest peticion) {
        return servicioSanitario.pendientes(filtro.obtenerFinca(peticion));
    }
}
