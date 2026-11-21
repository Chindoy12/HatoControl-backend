package com.hatocontrol.negocio;

import com.hatocontrol.accesodatos.IRepositorioAnimales;
import com.hatocontrol.accesodatos.IRepositorioSanitario;
import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.EstadoActividad;
import com.hatocontrol.modelo.RegistroSanitario;
import com.hatocontrol.modelo.TipoEventoSanitario;
import com.hatocontrol.negocio.excepciones.RecursoNoEncontradoException;
import com.hatocontrol.negocio.excepciones.ReglaNegocioException;
import com.hatocontrol.presentacion.dto.EventoSanitarioRequest;
import com.hatocontrol.presentacion.dto.EventoSanitarioResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Control sanitario: HU_006 a HU_009. */
@Service
@Transactional
public class ServicioSanitario {

    private final IRepositorioSanitario sanitario;
    private final IRepositorioAnimales animales;
    private final Clock reloj;

    public ServicioSanitario(IRepositorioSanitario sanitario, IRepositorioAnimales animales, Clock reloj) {
        this.sanitario = sanitario;
        this.animales = animales;
        this.reloj = reloj;
    }

    /** Vacuna, enfermedad o tratamiento ya realizado (HU_006). */
    public EventoSanitarioResponse registrarEvento(Long fincaId, String codigoArete, EventoSanitarioRequest solicitud) {
        Animal animal = animalSeleccionado(fincaId, codigoArete);
        LocalDate fecha = solicitud.fechaRealizacion();
        if (fecha == null) {
            throw new ReglaNegocioException("Escribe la fecha del evento.");
        }
        RegistroSanitario registro = nuevo(animal, solicitud);
        registro.setFechaRealizacion(fecha);
        registro.setEstado(EstadoActividad.REALIZADA);
        if (registro.esPosteriorA(hoy())) {
            throw new ReglaNegocioException("La fecha del evento no puede ser posterior a hoy.");
        }
        sanitario.guardar(registro);
        return aRespuesta(registro);
    }

    /** Actividad para hacer después (HU_008). */
    public EventoSanitarioResponse programar(Long fincaId, String codigoArete, EventoSanitarioRequest solicitud) {
        Animal animal = animalSeleccionado(fincaId, codigoArete);
        LocalDate fecha = solicitud.fechaProgramada();
        if (fecha == null) {
            throw new ReglaNegocioException("Escribe la fecha programada.");
        }
        if (fecha.isBefore(hoy())) {
            throw new ReglaNegocioException("La fecha programada no puede ser anterior a hoy.");
        }
        RegistroSanitario registro = nuevo(animal, solicitud);
        registro.setFechaProgramada(fecha);
        registro.setEstado(EstadoActividad.PROGRAMADA);
        sanitario.guardar(registro);
        return aRespuesta(registro);
    }

    public EventoSanitarioResponse marcarRealizada(Long fincaId, Long eventoId, LocalDate fechaRealizacion) {
        RegistroSanitario registro = sanitario.buscarPorId(eventoId)
                .filter(r -> r.getAnimal().getFinca().getId().equals(fincaId))
                .orElseThrow(() -> new RecursoNoEncontradoException("La actividad no existe en la finca."));
        if (registro.getEstado() == EstadoActividad.REALIZADA) {
            throw new ReglaNegocioException("La actividad ya está marcada como realizada.");
        }
        if (fechaRealizacion == null) {
            throw new ReglaNegocioException("Escribe la fecha de realización.");
        }
        if (fechaRealizacion.isAfter(hoy())) {
            throw new ReglaNegocioException("La fecha de realización no puede ser posterior a hoy.");
        }
        registro.marcarRealizada(fechaRealizacion);
        sanitario.guardar(registro);
        return aRespuesta(registro);
    }

    /** Solo eventos realizados, del más reciente al más antiguo; las programadas no aparecen (HU_007). */
    @Transactional(readOnly = true)
    public List<EventoSanitarioResponse> historial(Long fincaId, String codigoArete, TipoEventoSanitario tipo) {
        Animal animal = buscar(fincaId, codigoArete);
        return sanitario.historial(animal.getId()).stream()
                .filter(r -> r.getEstado() == EstadoActividad.REALIZADA)
                .filter(r -> tipo == null || r.getTipo() == tipo)
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoSanitarioResponse> proximas(Long fincaId, int dias) {
        if (dias < 1) {
            throw new ReglaNegocioException("El número de días debe ser mayor que cero.");
        }
        return sanitario.proximas(fincaId, hoy(), dias).stream().map(this::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<EventoSanitarioResponse> pendientes(Long fincaId) {
        return sanitario.pendientes(fincaId, hoy()).stream().map(this::aRespuesta).toList();
    }

    // ---- apoyo ----

    private LocalDate hoy() {
        return LocalDate.now(reloj);
    }

    private Animal animalSeleccionado(Long fincaId, String codigoArete) {
        if (codigoArete == null || codigoArete.isBlank()) {
            throw new ReglaNegocioException("Debes seleccionar un animal registrado.");
        }
        return animales.buscarPorArete(fincaId, ServicioAnimales.normalizarArete(codigoArete))
                .orElseThrow(() -> new ReglaNegocioException("Debes seleccionar un animal registrado."));
    }

    private Animal buscar(Long fincaId, String codigoArete) {
        String arete = ServicioAnimales.normalizarArete(codigoArete);
        return animales.buscarPorArete(fincaId, arete)
                .orElseThrow(() -> new RecursoNoEncontradoException("El animal " + arete + " no está registrado en la finca."));
    }

    private static RegistroSanitario nuevo(Animal animal, EventoSanitarioRequest solicitud) {
        RegistroSanitario registro = new RegistroSanitario();
        registro.setAnimal(animal);
        registro.setTipo(solicitud.tipo());
        registro.setDescripcion(solicitud.descripcion().trim());
        registro.setObservaciones(solicitud.observaciones() == null || solicitud.observaciones().isBlank() ? null : solicitud.observaciones().trim());
        return registro;
    }

    private EventoSanitarioResponse aRespuesta(RegistroSanitario r) {
        Animal a = r.getAnimal();
        return new EventoSanitarioResponse(r.getId(), a.getCodigoArete(), a.getNombre(), r.getTipo(), r.getDescripcion(),
                r.getFechaProgramada(), r.getFechaRealizacion(), r.getEstado(), r.getObservaciones());
    }
}
