package com.hatocontrol.negocio;

import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.RegistroLeche;
import com.hatocontrol.presentacion.dto.PromedioLecheResponse;
import java.time.LocalDate;
import java.util.List;

public interface IPeriodoPromedio {

    /** Devuelve [desde, hasta] del periodo que contiene la fecha. */
    LocalDate[] rango(Animal vaca, LocalDate fecha);

    PromedioLecheResponse promediar(List<RegistroLeche> registros);
}
