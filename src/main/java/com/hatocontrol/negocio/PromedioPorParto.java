package com.hatocontrol.negocio;

import com.hatocontrol.accesodatos.IRepositorioPartos;
import com.hatocontrol.modelo.Parto;
import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.RegistroLeche;
import com.hatocontrol.presentacion.dto.LecheResponse;
import com.hatocontrol.presentacion.dto.PromedioLecheResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

/** Lactancia del parto más reciente hasta la fecha: desde el parto hasta el día anterior al parto siguiente. */
@Component
public class PromedioPorParto implements IPeriodoPromedio {

    private final IRepositorioPartos partos;

    public PromedioPorParto(IRepositorioPartos partos) {
        this.partos = partos;
    }

    @Override
    public LocalDate[] rango(Animal vaca, LocalDate fecha) {
        List<Parto> lista = partos.listarPorMadre(vaca.getId());
        Parto actual = null;
        LocalDate fin = fecha;
        for (Parto p : lista) {
            if (!p.getFecha().isAfter(fecha)) {
                actual = p;
            } else {
                fin = p.getFecha().minusDays(1);
                break;
            }
        }
        return actual == null ? null : new LocalDate[] {actual.getFecha(), fin};
    }

    @Override
    public PromedioLecheResponse promediar(List<RegistroLeche> registros) {
        double total = registros.stream().mapToDouble(RegistroLeche::getLitros).sum();
        double promedio = registros.isEmpty() ? 0 : Math.round(total / registros.size() * 10) / 10.0;
        List<LecheResponse> diarios = registros.stream().map(r -> new LecheResponse(r.getFecha(), r.getLitros())).toList();
        return new PromedioLecheResponse("PARTO", registros.size(), total, promedio, diarios);
    }
}
