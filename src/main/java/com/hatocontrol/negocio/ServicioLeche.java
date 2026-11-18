package com.hatocontrol.negocio;

import com.hatocontrol.accesodatos.IRepositorioAnimales;
import com.hatocontrol.accesodatos.IRepositorioLeche;
import com.hatocontrol.accesodatos.IRepositorioPartos;
import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.Parto;
import com.hatocontrol.modelo.RegistroLeche;
import com.hatocontrol.negocio.excepciones.RecursoNoEncontradoException;
import com.hatocontrol.negocio.excepciones.ReglaNegocioException;
import com.hatocontrol.presentacion.dto.LecheRequest;
import com.hatocontrol.presentacion.dto.LecheResponse;
import com.hatocontrol.presentacion.dto.PromedioLecheResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Leche: HU_018 y HU_019. */
@Service
@Transactional
public class ServicioLeche {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final IRepositorioLeche leche;
    private final IRepositorioAnimales animales;
    private final IRepositorioPartos partos;
    private final Map<String, IPeriodoPromedio> periodos;
    private final Clock reloj;

    public ServicioLeche(IRepositorioLeche leche, IRepositorioAnimales animales, IRepositorioPartos partos,
            PromedioSemanal semanal, PromedioMensual mensual, PromedioPorParto porParto, Clock reloj) {
        this.leche = leche;
        this.animales = animales;
        this.partos = partos;
        this.periodos = Map.of("SEMANA", semanal, "MES", mensual, "PARTO", porParto);
        this.reloj = reloj;
    }

    public LecheResponse registrar(Long fincaId, LecheRequest solicitud) {
        Animal vaca = animales.buscarPorArete(fincaId, ServicioAnimales.normalizarArete(solicitud.codigoArete()))
                .orElseThrow(() -> new ReglaNegocioException("Selecciona una vaca registrada."));
        String nombre = ServicioAnimales.nombreCorto(vaca);
        if (!vaca.esHembraDeLeche()) {
            throw new ReglaNegocioException("No se puede registrar leche de " + nombre + ": su propósito es "
                    + vaca.getProposito().name().toLowerCase().replace("padron", "padrón") + ", no leche.");
        }
        List<Parto> partosVaca = partos.listarPorMadre(vaca.getId());
        if (partosVaca.isEmpty()) {
            throw new ReglaNegocioException("No se puede registrar leche de " + nombre + ": no tiene partos registrados.");
        }
        Parto parto = solicitud.fechaParto() == null
                ? partosVaca.getLast()
                : partosVaca.stream().filter(p -> p.getFecha().equals(solicitud.fechaParto())).findFirst()
                        .orElseThrow(() -> new ReglaNegocioException("Ese parto no está registrado para " + nombre + "."));
        LocalDate fecha = solicitud.fecha();
        if (fecha.isAfter(LocalDate.now(reloj))) {
            throw new ReglaNegocioException("La fecha no puede ser posterior a hoy.");
        }
        if (fecha.isBefore(parto.getFecha())) {
            throw new ReglaNegocioException("La fecha no puede ser anterior al parto (" + parto.getFecha().format(FECHA) + ").");
        }
        if (leche.existeEnFecha(vaca.getId(), fecha)) {
            throw new ReglaNegocioException("La leche del " + fecha.format(FECHA) + " ya está registrada para " + nombre + ".");
        }
        RegistroLeche registro = new RegistroLeche();
        registro.setAnimal(vaca);
        registro.setParto(parto);
        registro.setFecha(fecha);
        registro.setLitros(solicitud.litros());
        leche.guardar(registro);
        return new LecheResponse(registro.getFecha(), registro.getLitros());
    }

    /**
     * Promedio del periodo (SEMANA, MES o PARTO) que contiene la fecha; sin fecha, el periodo actual.
     * Solo cuenta los días con registro (HU_019). Sin registros: diasConRegistro = 0 y no hay promedio.
     */
    @Transactional(readOnly = true)
    public PromedioLecheResponse promedio(Long fincaId, String codigoArete, String periodo, LocalDate fecha) {
        IPeriodoPromedio calculo = periodos.get(periodo == null ? "" : periodo.toUpperCase());
        if (calculo == null) {
            throw new ReglaNegocioException("El periodo debe ser SEMANA, MES o PARTO.");
        }
        String arete = ServicioAnimales.normalizarArete(codigoArete);
        Animal vaca = animales.buscarPorArete(fincaId, arete)
                .orElseThrow(() -> new RecursoNoEncontradoException("El animal " + arete + " no está registrado en la finca."));
        LocalDate[] rango = calculo.rango(vaca, fecha == null ? LocalDate.now(reloj) : fecha);
        List<RegistroLeche> registros = rango == null ? List.of() : leche.listarEntre(vaca.getId(), rango[0], rango[1]);
        return calculo.promediar(registros);
    }
}
