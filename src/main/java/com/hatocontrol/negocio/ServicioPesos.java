package com.hatocontrol.negocio;

import com.hatocontrol.accesodatos.IRepositorioAnimales;
import com.hatocontrol.accesodatos.IRepositorioPesos;
import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.RegistroPeso;
import com.hatocontrol.negocio.excepciones.RecursoNoEncontradoException;
import com.hatocontrol.negocio.excepciones.ReglaNegocioException;
import com.hatocontrol.presentacion.dto.PesoRequest;
import com.hatocontrol.presentacion.dto.PesoResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioPesos {

    private final IRepositorioPesos pesos;
    private final IRepositorioAnimales animales;
    private final Clock reloj;

    public ServicioPesos(IRepositorioPesos pesos, IRepositorioAnimales animales, Clock reloj) {
        this.pesos = pesos;
        this.animales = animales;
        this.reloj = reloj;
    }

    public PesoResponse registrar(Long fincaId, String codigoArete, PesoRequest solicitud) {
        if (codigoArete == null || codigoArete.isBlank()) {
            throw new ReglaNegocioException("Debes seleccionar un animal registrado.");
        }
        Animal animal = animales.buscarPorArete(fincaId, ServicioAnimales.normalizarArete(codigoArete))
                .orElseThrow(() -> new ReglaNegocioException("Debes seleccionar un animal registrado."));
        RegistroPeso registro = new RegistroPeso();
        registro.setAnimal(animal);
        registro.setPesoKg(solicitud.pesoKg());
        registro.setFechaMedicion(solicitud.fechaMedicion());
        if (registro.esPosteriorA(LocalDate.now(reloj))) {
            throw new ReglaNegocioException("La fecha de medición no puede ser posterior a hoy.");
        }
        pesos.guardar(registro);
        return historialConDiferencias(fincaId, animal.getCodigoArete()).stream()
                .filter(p -> p.fechaMedicion().equals(registro.getFechaMedicion()) && p.pesoKg() == registro.getPesoKg())
                .reduce((primero, ultimo) -> ultimo)
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<PesoResponse> historialConDiferencias(Long fincaId, String codigoArete) {
        String arete = ServicioAnimales.normalizarArete(codigoArete);
        Animal animal = animales.buscarPorArete(fincaId, arete)
                .orElseThrow(() -> new RecursoNoEncontradoException("El animal " + arete + " no está registrado en la finca."));
        List<PesoResponse> resultado = new ArrayList<>();
        RegistroPeso anterior = null;
        for (RegistroPeso actual : pesos.historial(animal.getId())) {
            Double diferencia = anterior == null ? null : actual.diferenciaCon(anterior);
            resultado.add(new PesoResponse(actual.getPesoKg(), actual.getFechaMedicion(), diferencia));
            anterior = actual;
        }
        return resultado;
    }
}
