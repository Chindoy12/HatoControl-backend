package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioReproduccion;
import com.hatocontrol.presentacion.dto.PartoRequest;
import com.hatocontrol.presentacion.dto.PartoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/partos")
public class ControladorReproduccion {

    private final ServicioReproduccion servicioReproduccion;
    private final FiltroAutenticacion filtro;

    public ControladorReproduccion(ServicioReproduccion servicioReproduccion, FiltroAutenticacion filtro) {
        this.servicioReproduccion = servicioReproduccion;
        this.filtro = filtro;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartoResponse registrarParto(@Valid @RequestBody PartoRequest solicitud, HttpServletRequest peticion) {
        return servicioReproduccion.registrarParto(filtro.obtenerFinca(peticion), solicitud);
    }

    @GetMapping("/madres/{codigoArete}")
    public List<PartoResponse> historialReproductivo(@PathVariable String codigoArete, HttpServletRequest peticion) {
        return servicioReproduccion.historialReproductivo(filtro.obtenerFinca(peticion), codigoArete);
    }
}
