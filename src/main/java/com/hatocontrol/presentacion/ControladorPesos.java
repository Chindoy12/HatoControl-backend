package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioPesos;
import com.hatocontrol.presentacion.dto.PesoRequest;
import com.hatocontrol.presentacion.dto.PesoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animales/{codigoArete}/pesos")
public class ControladorPesos {

    private final ServicioPesos servicioPesos;
    private final FiltroAutenticacion filtro;

    public ControladorPesos(ServicioPesos servicioPesos, FiltroAutenticacion filtro) {
        this.servicioPesos = servicioPesos;
        this.filtro = filtro;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PesoResponse registrar(@PathVariable String codigoArete, @Valid @RequestBody PesoRequest solicitud, HttpServletRequest peticion) {
        return servicioPesos.registrar(filtro.obtenerFinca(peticion), codigoArete, solicitud);
    }

    @GetMapping
    public List<PesoResponse> historial(@PathVariable String codigoArete, HttpServletRequest peticion) {
        return servicioPesos.historialConDiferencias(filtro.obtenerFinca(peticion), codigoArete);
    }
}
