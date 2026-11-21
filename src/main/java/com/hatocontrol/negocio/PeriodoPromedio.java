package com.hatocontrol.negocio;

import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.RegistroLeche;
import com.hatocontrol.presentacion.dto.LecheResponse;
import com.hatocontrol.presentacion.dto.PromedioLecheResponse;
import java.time.LocalDate;
import java.util.List;

/**
 * Periodo para promediar la leche (HU_019). Patrón Estrategia con herencia:
 * cada subclase solo dice qué días abarca su periodo; el promedio se calcula igual para todas.
 * Solo cuentan los días con registro (HU_019 C2).
 */
public abstract class PeriodoPromedio {

    /** SEMANA, MES o PARTO. */
    public abstract String nombre();

    /** Devuelve [desde, hasta] del periodo que contiene la fecha, o null si no hay periodo. */
    public abstract LocalDate[] rango(Animal vaca, LocalDate fecha);

    public PromedioLecheResponse promediar(List<RegistroLeche> registros) {
        double total = registros.stream().mapToDouble(RegistroLeche::getLitros).sum();
        double promedio = registros.isEmpty() ? 0 : Math.round(total / registros.size() * 10) / 10.0;
        List<LecheResponse> diarios = registros.stream().map(r -> new LecheResponse(r.getFecha(), r.getLitros())).toList();
        return new PromedioLecheResponse(nombre(), registros.size(), total, promedio, diarios);
    }
}
