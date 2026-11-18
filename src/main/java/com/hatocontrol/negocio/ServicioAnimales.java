package com.hatocontrol.negocio;

import com.hatocontrol.accesodatos.IRepositorioAnimales;
import com.hatocontrol.accesodatos.IRepositorioFincas;
import com.hatocontrol.accesodatos.IRepositorioLeche;
import com.hatocontrol.accesodatos.IRepositorioPartos;
import com.hatocontrol.accesodatos.IRepositorioPesos;
import com.hatocontrol.accesodatos.IRepositorioSanitario;
import com.hatocontrol.modelo.Animal;
import com.hatocontrol.modelo.Proposito;
import com.hatocontrol.modelo.Sexo;
import com.hatocontrol.negocio.excepciones.RecursoNoEncontradoException;
import com.hatocontrol.negocio.excepciones.ReglaNegocioException;
import com.hatocontrol.presentacion.dto.AnimalRequest;
import com.hatocontrol.presentacion.dto.AnimalResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Animales de la finca: HU_001 a HU_005, HU_013 y HU_015. */
@Service
@Transactional
public class ServicioAnimales {

    private final IRepositorioAnimales animales;
    private final IRepositorioFincas fincas;
    private final IRepositorioSanitario sanitario;
    private final IRepositorioPesos pesos;
    private final IRepositorioPartos partos;
    private final IRepositorioLeche leche;
    private final Clock reloj;

    public ServicioAnimales(IRepositorioAnimales animales, IRepositorioFincas fincas, IRepositorioSanitario sanitario,
            IRepositorioPesos pesos, IRepositorioPartos partos, IRepositorioLeche leche, Clock reloj) {
        this.animales = animales;
        this.fincas = fincas;
        this.sanitario = sanitario;
        this.pesos = pesos;
        this.partos = partos;
        this.leche = leche;
        this.reloj = reloj;
    }

    public AnimalResponse registrar(Long fincaId, AnimalRequest solicitud) {
        String arete = normalizarArete(solicitud.codigoArete());
        validarDatos(solicitud);
        if (animales.buscarPorArete(fincaId, arete).isPresent()) {
            throw new ReglaNegocioException("El código de arete " + arete + " ya está registrado en la finca.");
        }
        Animal animal = new Animal();
        animal.setFinca(fincas.buscarPorId(fincaId).orElseThrow());
        animal.setCodigoArete(arete);
        copiarDatos(solicitud, animal);
        animales.guardar(animal);
        return aRespuesta(animal);
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> listar(Long fincaId, String filtro, Sexo sexo, Proposito proposito) {
        return animales.listar(fincaId, filtro).stream()
                .filter(a -> sexo == null || a.getSexo() == sexo)
                .filter(a -> proposito == null || a.getProposito() == proposito)
                .map(this::aRespuesta)
                .toList();
    }

    public AnimalResponse actualizar(Long fincaId, String codigoArete, AnimalRequest solicitud) {
        Animal animal = buscar(fincaId, codigoArete);
        String areteNuevo = normalizarArete(solicitud.codigoArete());
        if (!areteNuevo.equalsIgnoreCase(animal.getCodigoArete()) && animales.buscarPorArete(fincaId, areteNuevo).isPresent()) {
            throw new ReglaNegocioException("El código de arete " + areteNuevo + " ya está registrado en la finca. Se conserva el arete "
                    + animal.getCodigoArete() + ".");
        }
        if (solicitud.sexo() != animal.getSexo() && !admite(solicitud.sexo(), solicitud.proposito())) {
            throw new ReglaNegocioException("Cambiaste el sexo. Elige de nuevo el propósito: " + opciones(solicitud.sexo()) + ".");
        }
        validarDatos(solicitud);
        animal.setCodigoArete(areteNuevo);
        copiarDatos(solicitud, animal);
        animales.guardar(animal);
        return aRespuesta(animal);
    }

    public void eliminar(Long fincaId, String codigoArete) {
        Animal animal = buscar(fincaId, codigoArete);
        List<String> registros = new ArrayList<>();
        if (sanitario.existenPara(animal.getId())) {
            registros.add("registros sanitarios");
        }
        if (pesos.existenPara(animal.getId())) {
            registros.add("mediciones de peso");
        }
        if (partos.existenPara(animal.getId())) {
            registros.add("partos y crías");
        }
        if (leche.existenPara(animal.getId())) {
            registros.add("registros de leche");
        }
        if (!registros.isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar " + nombreCorto(animal) + ": tiene " + String.join(", ", registros) + ".");
        }
        animales.eliminar(animal);
    }

    @Transactional(readOnly = true)
    public AnimalResponse paginaCompleta(Long fincaId, String codigoArete) {
        return aRespuesta(buscar(fincaId, codigoArete));
    }

    public AnimalResponse pasarAEngorde(Long fincaId, String codigoArete) {
        Animal animal = buscar(fincaId, codigoArete);
        if (animal.getProposito() == Proposito.ENGORDE) {
            throw new ReglaNegocioException(nombreCorto(animal) + " ya es de engorde: no tiene cambios de propósito.");
        }
        animal.pasarAEngorde();
        animales.guardar(animal);
        return aRespuesta(animal);
    }

    // ---- apoyo ----

    Animal buscar(Long fincaId, String codigoArete) {
        String arete = normalizarArete(codigoArete);
        return animales.buscarPorArete(fincaId, arete)
                .orElseThrow(() -> new RecursoNoEncontradoException("El animal " + arete + " no está registrado en la finca."));
    }

    AnimalResponse aRespuesta(Animal a) {
        return new AnimalResponse(a.getCodigoArete(), a.getNombre(), a.getSexo(), a.getFechaNacimiento(),
                a.calcularEdadMeses(LocalDate.now(reloj)), a.getProposito());
    }

    static String normalizarArete(String arete) {
        return arete == null ? "" : arete.trim().toUpperCase();
    }

    static String nombreCorto(Animal a) {
        return a.getNombre() == null || a.getNombre().isBlank() ? a.getCodigoArete() : a.getNombre();
    }

    static boolean admite(Sexo sexo, Proposito proposito) {
        Animal prueba = new Animal();
        prueba.setSexo(sexo);
        return prueba.admiteProposito(proposito);
    }

    static String opciones(Sexo sexo) {
        return sexo == Sexo.MACHO ? "engorde o padrón" : "engorde o leche";
    }

    /** Reglas de HU_001 y HU_004 que comparten registrar, actualizar y las crías de un parto. */
    void validarDatos(AnimalRequest solicitud) {
        if (!admite(solicitud.sexo(), solicitud.proposito())) {
            throw new ReglaNegocioException((solicitud.sexo() == Sexo.MACHO ? "Un macho" : "Una hembra")
                    + " solo puede ser de " + opciones(solicitud.sexo()) + ".");
        }
        if (solicitud.fechaNacimiento() != null && solicitud.fechaNacimiento().isAfter(LocalDate.now(reloj))) {
            throw new ReglaNegocioException("La fecha de nacimiento no puede ser posterior a hoy.");
        }
    }

    private static void copiarDatos(AnimalRequest solicitud, Animal animal) {
        animal.setNombre(solicitud.nombre() == null || solicitud.nombre().isBlank() ? null : solicitud.nombre().trim());
        animal.setSexo(solicitud.sexo());
        animal.setFechaNacimiento(solicitud.fechaNacimiento());
        animal.setProposito(solicitud.proposito());
    }
}
