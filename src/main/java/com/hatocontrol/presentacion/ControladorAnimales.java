package com.hatocontrol.presentacion;

import com.hatocontrol.modelo.Proposito;
import com.hatocontrol.modelo.Sexo;
import com.hatocontrol.negocio.ServicioAnimales;
import com.hatocontrol.presentacion.dto.AnimalRequest;
import com.hatocontrol.presentacion.dto.AnimalResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animales")
public class ControladorAnimales {

    private final ServicioAnimales servicioAnimales;
    private final FiltroAutenticacion filtro;

    public ControladorAnimales(ServicioAnimales servicioAnimales, FiltroAutenticacion filtro) {
        this.servicioAnimales = servicioAnimales;
        this.filtro = filtro;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalResponse registrar(@Valid @RequestBody AnimalRequest solicitud, HttpServletRequest peticion) {
        return servicioAnimales.registrar(filtro.obtenerFinca(peticion), solicitud);
    }

    @GetMapping
    public List<AnimalResponse> listar(@RequestParam(required = false) String filtro, @RequestParam(required = false) Sexo sexo,
            @RequestParam(required = false) Proposito proposito, HttpServletRequest peticion) {
        return servicioAnimales.listar(this.filtro.obtenerFinca(peticion), filtro, sexo, proposito);
    }

    @GetMapping("/{codigoArete}")
    public AnimalResponse verPagina(@PathVariable String codigoArete, HttpServletRequest peticion) {
        return servicioAnimales.paginaCompleta(filtro.obtenerFinca(peticion), codigoArete);
    }

    @PutMapping("/{codigoArete}")
    public AnimalResponse actualizar(@PathVariable String codigoArete, @Valid @RequestBody AnimalRequest solicitud, HttpServletRequest peticion) {
        return servicioAnimales.actualizar(filtro.obtenerFinca(peticion), codigoArete, solicitud);
    }

    @DeleteMapping("/{codigoArete}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String codigoArete, HttpServletRequest peticion) {
        servicioAnimales.eliminar(filtro.obtenerFinca(peticion), codigoArete);
    }

    @PostMapping("/{codigoArete}/engorde")
    public AnimalResponse pasarAEngorde(@PathVariable String codigoArete, HttpServletRequest peticion) {
        return servicioAnimales.pasarAEngorde(filtro.obtenerFinca(peticion), codigoArete);
    }
}
