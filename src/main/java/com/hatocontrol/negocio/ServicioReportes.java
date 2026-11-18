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

/**
 * Dashboard (HU_012) y reportes (HU_014). Cada fila es una lista de celdas separadas por tabulador.
 * Solo usa datos guardados: nunca estima valores. Sin filas = «sin registros».
 */
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

    /**
     * Filas del dashboard, la primera celda dice la sección:
     * resumen·animales|vacunas|enfermedades|tratamientos·cantidad;
     * actividad·estado(VENCIDA|PROXIMA)·fecha·arete·nombre·tipo·descripción;
     * peso·arete·nombre·kg·fecha.
     */
    public ReporteResponse dashboard(Long fincaId) {
        LocalDate hoy = LocalDate.now(reloj);
        List<Animal> lista = animales.listar(fincaId, null);
        Map<TipoEventoSanitario, Integer> realizados = new EnumMap<>(TipoEventoSanitario.class);
        for (TipoEventoSanitario t : TipoEventoSanitario.values()) {
            realizados.put(t, 0);
        }
        List<String> pesosFilas = new ArrayList<>();
        for (Animal a : lista) {
            for (RegistroSanitario r : sanitario.historial(a.getId())) {
                if (r.getEstado() == EstadoActividad.REALIZADA) {
                    realizados.merge(r.getTipo(), 1, Integer::sum);
                }
            }
            List<RegistroPeso> historial = pesos.historial(a.getId());
            if (!historial.isEmpty()) {
                RegistroPeso ultimo = historial.getLast();
                pesosFilas.add(fila("peso", a.getCodigoArete(), texto(a.getNombre()), numero(ultimo.getPesoKg()), ultimo.getFechaMedicion().toString()));
            }
        }
        List<String> filas = new ArrayList<>();
        filas.add(fila("resumen", "animales", String.valueOf(lista.size())));
        filas.add(fila("resumen", "vacunas", String.valueOf(realizados.get(TipoEventoSanitario.VACUNA))));
        filas.add(fila("resumen", "enfermedades", String.valueOf(realizados.get(TipoEventoSanitario.ENFERMEDAD))));
        filas.add(fila("resumen", "tratamientos", String.valueOf(realizados.get(TipoEventoSanitario.TRATAMIENTO))));
        for (EventoSanitarioResponse e : servicioSanitario.pendientes(fincaId)) {
            filas.add(fila("actividad", "VENCIDA", e.fechaProgramada().toString(), e.codigoArete(), texto(e.nombreAnimal()), e.tipo().name(), e.descripcion()));
        }
        for (EventoSanitarioResponse e : servicioSanitario.proximas(fincaId, 15)) {
            filas.add(fila("actividad", "PROXIMA", e.fechaProgramada().toString(), e.codigoArete(), texto(e.nombreAnimal()), e.tipo().name(), e.descripcion()));
        }
        filas.addAll(pesosFilas);
        return new ReporteResponse("DASHBOARD", hoy, filas);
    }

    /** ANIMALES: arete·nombre·sexo·propósito·edad. SANITARIO: fecha·tipo·descripción·observaciones. PESO: fecha·kg·variación. */
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
