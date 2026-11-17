package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioPesos;
import com.hatocontrol.presentacion.dto.PesoRequest;
import com.hatocontrol.presentacion.dto.PesoResponse;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animales/{codigoArete}/pesos")
public class ControladorPesos {

    private final ServicioPesos servicioPesos;

    public ControladorPesos(ServicioPesos servicioPesos) {
        this.servicioPesos = servicioPesos;
    }

    @PostMapping
    public PesoResponse registrar(@PathVariable String codigoArete, @RequestBody PesoRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @GetMapping
    public List<PesoResponse> historial(@PathVariable String codigoArete) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
