package com.hatocontrol.negocio;

import com.hatocontrol.modelo.Animal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/** Semana de lunes a domingo que contiene la fecha. */
@Component
public class PromedioSemanal extends PeriodoPromedio {

    @Override
    public String nombre() {
        return "SEMANA";
    }

    @Override
    public LocalDate[] rango(Animal vaca, LocalDate fecha) {
        LocalDate lunes = fecha.with(DayOfWeek.MONDAY);
        return new LocalDate[] {lunes, lunes.plusDays(6)};
    }
}
