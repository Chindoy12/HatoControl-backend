package com.hatocontrol.negocio;

import com.hatocontrol.accesodatos.IRepositorioPartos;
import com.hatocontrol.modelo.Parto;
import com.hatocontrol.modelo.Animal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PromedioPorParto extends PeriodoPromedio {

    private final IRepositorioPartos partos;

    public PromedioPorParto(IRepositorioPartos partos) {
        this.partos = partos;
    }

    @Override
    public String nombre() {
        return "PARTO";
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
}
