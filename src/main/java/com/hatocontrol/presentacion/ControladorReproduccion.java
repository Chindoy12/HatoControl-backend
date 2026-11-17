package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioReproduccion;
import com.hatocontrol.presentacion.dto.PartoRequest;
import com.hatocontrol.presentacion.dto.PartoResponse;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/partos")
public class ControladorReproduccion {

    private final ServicioReproduccion servicioReproduccion;

    public ControladorReproduccion(ServicioReproduccion servicioReproduccion) {
        this.servicioReproduccion = servicioReproduccion;
    }

    @PostMapping
    public PartoResponse registrarParto(@RequestBody PartoRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @GetMapping("/madres/{codigoArete}")
    public List<PartoResponse> historialReproductivo(@PathVariable String codigoArete) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
