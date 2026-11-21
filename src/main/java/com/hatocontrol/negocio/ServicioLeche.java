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
import java.util.stream.Collectors;
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
    private final Map<String, PeriodoPromedio> periodos;
    private final Clock reloj;

    /** Spring entrega todas las subclases de PeriodoPromedio; un periodo nuevo solo necesita su subclase. */
    public ServicioLeche(IRepositorioLeche leche, IRepositorioAnimales animales, IRepositorioPartos partos,
            List<PeriodoPromedio> periodos, Clock reloj) {
        this.leche = leche;
        this.animales = animales;
        this.partos = partos;
        this.periodos = periodos.stream().collect(Collectors.toMap(PeriodoPromedio::nombre, p -> p));
        this.reloj = reloj;
    }

    public LecheResponse registrar(Long fincaId, LecheRequest solicitud) {
        Animal vaca = buscarVacaDeLeche(fincaId, solicitud.codigoArete());
        Parto parto = elegirParto(vaca, solicitud.fechaParto());
        RegistroLeche registro = new RegistroLeche();
        registro.setAnimal(vaca);
        registro.setParto(parto);
        registro.setFecha(solicitud.fecha());
        registro.setLitros(solicitud.litros());
        validarFecha(registro, parto);
        leche.guardar(registro);
        return new LecheResponse(registro.getFecha(), registro.getLitros());
    }

    private Animal buscarVacaDeLeche(Long fincaId, String codigoArete) {
        Animal vaca = animales.buscarPorArete(fincaId, ServicioAnimales.normalizarArete(codigoArete))
                .orElseThrow(() -> new ReglaNegocioException("Selecciona una vaca registrada."));
        if (!vaca.esHembraDeLeche()) {
            throw new ReglaNegocioException("No se puede registrar leche de " + ServicioAnimales.nombreCorto(vaca)
                    + ": su propósito es " + vaca.getProposito().name().toLowerCase().replace("padron", "padrón") + ", no leche.");
        }
        return vaca;
    }

    /** El parto elegido o, si no se eligió, el más reciente. */
    private Parto elegirParto(Animal vaca, LocalDate fechaParto) {
        List<Parto> partosVaca = partos.listarPorMadre(vaca.getId());
        String nombre = ServicioAnimales.nombreCorto(vaca);
        if (partosVaca.isEmpty()) {
            throw new ReglaNegocioException("No se puede registrar leche de " + nombre + ": no tiene partos registrados.");
        }
        if (fechaParto == null) {
            return partosVaca.getLast();
        }
        return partosVaca.stream().filter(p -> p.getFecha().equals(fechaParto)).findFirst()
                .orElseThrow(() -> new ReglaNegocioException("Ese parto no está registrado para " + nombre + "."));
    }

    /** No posterior a hoy, no anterior al parto y un solo registro por día (RF47). */
    private void validarFecha(RegistroLeche registro, Parto parto) {
        LocalDate fecha = registro.getFecha();
        if (registro.esPosteriorA(LocalDate.now(reloj))) {
            throw new ReglaNegocioException("La fecha no puede ser posterior a hoy.");
        }
        if (fecha.isBefore(parto.getFecha())) {
            throw new ReglaNegocioException("La fecha no puede ser anterior al parto (" + parto.getFecha().format(FECHA) + ").");
        }
        if (leche.existeEnFecha(registro.getAnimal().getId(), fecha)) {
            throw new ReglaNegocioException("La leche del " + fecha.format(FECHA) + " ya está registrada para "
                    + ServicioAnimales.nombreCorto(registro.getAnimal()) + ".");
        }
    }

    /**
     * Promedio del periodo (SEMANA, MES o PARTO) que contiene la fecha; sin fecha, el periodo actual.
     * Solo cuenta los días con registro (HU_019). Sin registros: diasConRegistro = 0 y no hay promedio.
     */
    @Transactional(readOnly = true)
    public PromedioLecheResponse promedio(Long fincaId, String codigoArete, String periodo, LocalDate fecha) {
        PeriodoPromedio calculo = periodos.get(periodo == null ? "" : periodo.toUpperCase());
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
