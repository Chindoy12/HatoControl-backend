package com.hatocontrol.negocio;

import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.RegistroLeche;
import com.hatocontrol.presentacion.dto.PromedioLecheResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PromedioPorParto implements IPeriodoPromedio {

    @Override
    public LocalDate[] rango(Animal vaca, LocalDate fecha) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public PromedioLecheResponse promediar(List<RegistroLeche> registros) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
