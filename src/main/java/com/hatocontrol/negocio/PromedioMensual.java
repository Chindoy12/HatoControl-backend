package com.hatocontrol.negocio;

import com.hatocontrol.modelo.Animal;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/** Mes calendario que contiene la fecha. */
@Component
public class PromedioMensual extends PeriodoPromedio {

    @Override
    public String nombre() {
        return "MES";
    }

    @Override
    public LocalDate[] rango(Animal vaca, LocalDate fecha) {
        return new LocalDate[] {fecha.withDayOfMonth(1), fecha.withDayOfMonth(fecha.lengthOfMonth())};
    }
}
