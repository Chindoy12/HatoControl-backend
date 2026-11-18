package com.hatocontrol.negocio;


import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.RegistroLeche;
import com.hatocontrol.presentacion.dto.LecheResponse;
import com.hatocontrol.presentacion.dto.PromedioLecheResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

/** Semana de lunes a domingo que contiene la fecha. */
@Component
public class PromedioSemanal implements IPeriodoPromedio {

    @Override
    public LocalDate[] rango(Animal vaca, LocalDate fecha) {
        LocalDate lunes = fecha.with(java.time.DayOfWeek.MONDAY);
        return new LocalDate[] {lunes, lunes.plusDays(6)};
    }

    @Override
    public PromedioLecheResponse promediar(List<RegistroLeche> registros) {
        double total = registros.stream().mapToDouble(RegistroLeche::getLitros).sum();
        double promedio = registros.isEmpty() ? 0 : Math.round(total / registros.size() * 10) / 10.0;
        List<LecheResponse> diarios = registros.stream().map(r -> new LecheResponse(r.getFecha(), r.getLitros())).toList();
        return new PromedioLecheResponse("SEMANA", registros.size(), total, promedio, diarios);
    }
}
