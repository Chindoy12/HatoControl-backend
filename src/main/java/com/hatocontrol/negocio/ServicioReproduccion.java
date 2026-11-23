package com.hatocontrol.negocio;

import com.hatocontrol.accesodatos.IRepositorioAnimales;
import com.hatocontrol.accesodatos.IRepositorioPartos;
import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.Parto;
import com.hatocontrol.modelo.Sexo;
import com.hatocontrol.negocio.excepciones.RecursoNoEncontradoException;
import com.hatocontrol.negocio.excepciones.ReglaNegocioException;
import com.hatocontrol.presentacion.dto.AnimalRequest;
import com.hatocontrol.presentacion.dto.AnimalResponse;
import com.hatocontrol.presentacion.dto.PartoRequest;
import com.hatocontrol.presentacion.dto.PartoResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioReproduccion {

    private final IRepositorioPartos partos;
    private final IRepositorioAnimales animales;
    private final ServicioAnimales servicioAnimales;
    private final Clock reloj;

    public ServicioReproduccion(IRepositorioPartos partos, IRepositorioAnimales animales, ServicioAnimales servicioAnimales, Clock reloj) {
        this.partos = partos;
        this.animales = animales;
        this.servicioAnimales = servicioAnimales;
        this.reloj = reloj;
    }

    public PartoResponse registrarParto(Long fincaId, PartoRequest solicitud) {
        Animal madre = buscarMadre(fincaId, solicitud);
        List<AnimalRequest> crias = solicitud.crias() == null ? List.of() : solicitud.crias();
        validarCrias(fincaId, crias, solicitud.fecha());
        Parto parto = new Parto();
        parto.setMadre(madre);
        parto.setFecha(solicitud.fecha());
        partos.guardar(parto);
        for (AnimalRequest cria : crias) {
            parto.getCrias().add(guardarCria(cria, parto));
        }
        return aRespuesta(parto);
    }

    private Animal buscarMadre(Long fincaId, PartoRequest solicitud) {
        Animal madre = animales.buscarPorArete(fincaId, ServicioAnimales.normalizarArete(solicitud.codigoAreteMadre()))
                .filter(a -> a.getSexo() == Sexo.HEMBRA)
                .orElseThrow(() -> new ReglaNegocioException("Selecciona una hembra registrada."));
        if (solicitud.fecha().isAfter(LocalDate.now(reloj))) {
            throw new ReglaNegocioException("La fecha del parto no puede ser posterior a hoy.");
        }
        return madre;
    }

    private void validarCrias(Long fincaId, List<AnimalRequest> crias, LocalDate fechaParto) {
        Set<String> aretes = new HashSet<>();
        for (AnimalRequest cria : crias) {
            String arete = ServicioAnimales.normalizarArete(cria.codigoArete());
            if (!aretes.add(arete) || animales.buscarPorArete(fincaId, arete).isPresent()) {
                throw new ReglaNegocioException("El código de arete " + arete + " ya está registrado en la finca.");
            }
            servicioAnimales.validarDatos(new AnimalRequest(arete, cria.nombre(), cria.sexo(), fechaParto, cria.proposito()));
        }
    }

    private Animal guardarCria(AnimalRequest cria, Parto parto) {
        Animal nueva = new Animal();
        nueva.setFinca(parto.getMadre().getFinca());
        nueva.setCodigoArete(ServicioAnimales.normalizarArete(cria.codigoArete()));
        nueva.setNombre(cria.nombre() == null || cria.nombre().isBlank() ? null : cria.nombre().trim());
        nueva.setSexo(cria.sexo());
        nueva.setProposito(cria.proposito());
        nueva.setFechaNacimiento(parto.getFecha());
        nueva.setParto(parto);
        animales.guardar(nueva);
        return nueva;
    }

    @Transactional(readOnly = true)
    public List<PartoResponse> historialReproductivo(Long fincaId, String codigoArete) {
        String arete = ServicioAnimales.normalizarArete(codigoArete);
        Animal animal = animales.buscarPorArete(fincaId, arete)
                .orElseThrow(() -> new RecursoNoEncontradoException("El animal " + arete + " no está registrado en la finca."));
        List<PartoResponse> resultado = new ArrayList<>();
        for (Parto parto : partos.listarPorMadre(animal.getId())) {
            resultado.add(aRespuesta(parto));
        }
        return resultado;
    }

    private PartoResponse aRespuesta(Parto parto) {
        List<AnimalResponse> crias = parto.getCrias().stream().map(servicioAnimales::aRespuesta).toList();
        return new PartoResponse(parto.getFecha(), crias);
    }
}
