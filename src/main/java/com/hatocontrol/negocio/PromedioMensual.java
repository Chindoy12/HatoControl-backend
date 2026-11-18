package com.hatocontrol.negocio;


import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.RegistroLeche;
import com.hatocontrol.presentacion.dto.LecheResponse;
import com.hatocontrol.presentacion.dto.PromedioLecheResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

/** Mes calendario que contiene la fecha. */
@Component
public class PromedioMensual implements IPeriodoPromedio {

    @Override
    public LocalDate[] rango(Animal vaca, LocalDate fecha) {
        return new LocalDate[] {fecha.withDayOfMonth(1), fecha.withDayOfMonth(fecha.lengthOfMonth())};
    }

    @Override
    public PromedioLecheResponse promediar(List<RegistroLeche> registros) {
        double total = registros.stream().mapToDouble(RegistroLeche::getLitros).sum();
        double promedio = registros.isEmpty() ? 0 : Math.round(total / registros.size() * 10) / 10.0;
        List<LecheResponse> diarios = registros.stream().map(r -> new LecheResponse(r.getFecha(), r.getLitros())).toList();
        return new PromedioLecheResponse("MES", registros.size(), total, promedio, diarios);
    }
}
