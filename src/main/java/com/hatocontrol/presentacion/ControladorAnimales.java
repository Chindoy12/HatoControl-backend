package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioAnimales;
import com.hatocontrol.presentacion.dto.AnimalRequest;
import com.hatocontrol.presentacion.dto.AnimalResponse;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animales")
public class ControladorAnimales {

    private final ServicioAnimales servicioAnimales;

    public ControladorAnimales(ServicioAnimales servicioAnimales) {
        this.servicioAnimales = servicioAnimales;
    }

    @PostMapping
    public AnimalResponse registrar(@RequestBody AnimalRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @GetMapping
    public List<AnimalResponse> listar(@RequestParam(required = false) String filtro) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @GetMapping("/{codigoArete}")
    public AnimalResponse verPagina(@PathVariable String codigoArete) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @PutMapping("/{codigoArete}")
    public AnimalResponse actualizar(@PathVariable String codigoArete, @RequestBody AnimalRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @DeleteMapping("/{codigoArete}")
    public void eliminar(@PathVariable String codigoArete) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @PostMapping("/{codigoArete}/engorde")
    public AnimalResponse pasarAEngorde(@PathVariable String codigoArete) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
