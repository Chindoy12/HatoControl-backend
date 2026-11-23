package com.hatocontrol.negocio;

import com.hatocontrol.accesodatos.IRepositorioAnimales;
import com.hatocontrol.accesodatos.IRepositorioPesos;
import com.hatocontrol.accesodatos.IRepositorioSanitario;
import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.EstadoActividad;
import com.hatocontrol.modelo.RegistroPeso;
import com.hatocontrol.modelo.RegistroSanitario;
import com.hatocontrol.modelo.TipoEventoSanitario;
import com.hatocontrol.negocio.excepciones.ReglaNegocioException;
import com.hatocontrol.presentacion.dto.AnimalResponse;
import com.hatocontrol.presentacion.dto.EventoSanitarioResponse;
import com.hatocontrol.presentacion.dto.PesoResponse;
import com.hatocontrol.presentacion.dto.ReporteRequest;
import com.hatocontrol.presentacion.dto.ReporteResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ServicioReportes {

    static final String SEP = "\t";

    private final IRepositorioAnimales animales;
    private final IRepositorioSanitario sanitario;
    private final IRepositorioPesos pesos;
    private final ServicioAnimales servicioAnimales;
    private final ServicioSanitario servicioSanitario;
    private final ServicioPesos servicioPesos;
    private final Clock reloj;

    public ServicioReportes(IRepositorioAnimales animales, IRepositorioSanitario sanitario, IRepositorioPesos pesos,
            ServicioAnimales servicioAnimales, ServicioSanitario servicioSanitario, ServicioPesos servicioPesos, Clock reloj) {
        this.animales = animales;
        this.sanitario = sanitario;
        this.pesos = pesos;
        this.servicioAnimales = servicioAnimales;
        this.servicioSanitario = servicioSanitario;
        this.servicioPesos = servicioPesos;
        this.reloj = reloj;
    }

    public ReporteResponse dashboard(Long fincaId) {
        List<Animal> lista = animales.listar(fincaId, null);
        List<String> filas = new ArrayList<>(filasResumen(lista));
        for (EventoSanitarioResponse e : servicioSanitario.pendientes(fincaId)) {
            filas.add(filaActividad("VENCIDA", e));
        }
        for (EventoSanitarioResponse e : servicioSanitario.proximas(fincaId, 15)) {
            filas.add(filaActividad("PROXIMA", e));
        }
        filas.addAll(filasUltimoPeso(lista));
        return new ReporteResponse("DASHBOARD", LocalDate.now(reloj), filas);
    }

    private List<String> filasResumen(List<Animal> lista) {
        Map<TipoEventoSanitario, Integer> realizados = new EnumMap<>(TipoEventoSanitario.class);
        for (TipoEventoSanitario t : TipoEventoSanitario.values()) {
            realizados.put(t, 0);
        }
        for (Animal a : lista) {
            for (RegistroSanitario r : sanitario.historial(a.getId())) {
                if (r.getEstado() == EstadoActividad.REALIZADA) {
                    realizados.merge(r.getTipo(), 1, Integer::sum);
                }
            }
        }
        return List.of(
                fila("resumen", "animales", String.valueOf(lista.size())),
                fila("resumen", "vacunas", String.valueOf(realizados.get(TipoEventoSanitario.VACUNA))),
                fila("resumen", "enfermedades", String.valueOf(realizados.get(TipoEventoSanitario.ENFERMEDAD))),
                fila("resumen", "tratamientos", String.valueOf(realizados.get(TipoEventoSanitario.TRATAMIENTO))));
    }

    private String filaActividad(String estado, EventoSanitarioResponse e) {
        return fila("actividad", estado, e.fechaProgramada().toString(), e.codigoArete(), texto(e.nombreAnimal()), e.tipo().name(), e.descripcion());
    }

    private List<String> filasUltimoPeso(List<Animal> lista) {
        List<String> filas = new ArrayList<>();
        for (Animal a : lista) {
            List<RegistroPeso> historial = pesos.historial(a.getId());
            if (!historial.isEmpty()) {
                RegistroPeso ultimo = historial.getLast();
                filas.add(fila("peso", a.getCodigoArete(), texto(a.getNombre()), numero(ultimo.getPesoKg()), ultimo.getFechaMedicion().toString()));
            }
        }
        return filas;
    }

    public ReporteResponse reporte(Long fincaId, ReporteRequest solicitud) {
        String tipo = solicitud.tipo().toUpperCase();
        List<String> filas = new ArrayList<>();
        switch (tipo) {
            case "ANIMALES" -> {
                for (AnimalResponse a : servicioAnimales.listar(fincaId, null, null, null)) {
                    filas.add(fila(a.codigoArete(), texto(a.nombre()), a.sexo().name(), a.proposito().name(),
                            a.edadMeses() == null ? "" : String.valueOf(a.edadMeses())));
                }
            }
            case "SANITARIO" -> {
                for (EventoSanitarioResponse e : servicioSanitario.historial(fincaId, arete(solicitud), null)) {
                    filas.add(fila(e.fechaRealizacion().toString(), e.tipo().name(), e.descripcion(), texto(e.observaciones())));
                }
            }
            case "PESO" -> {
                for (PesoResponse p : servicioPesos.historialConDiferencias(fincaId, arete(solicitud))) {
                    filas.add(fila(p.fechaMedicion().toString(), numero(p.pesoKg()), p.diferenciaKg() == null ? "" : numero(p.diferenciaKg())));
                }
            }
            default -> throw new ReglaNegocioException("El tipo de reporte debe ser ANIMALES, SANITARIO o PESO.");
        }
        return new ReporteResponse(tipo, LocalDate.now(reloj), filas);
    }

    private static String arete(ReporteRequest solicitud) {
        if (solicitud.codigoArete() == null || solicitud.codigoArete().isBlank()) {
            throw new ReglaNegocioException("Escribe el código de arete del animal.");
        }
        return solicitud.codigoArete();
    }

    private static String fila(String... celdas) {
        return String.join(SEP, celdas);
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor.replace(SEP, " ");
    }

    private static String numero(double valor) {
        return valor == Math.rint(valor) ? String.valueOf((long) valor) : String.valueOf(valor);
    }
}
