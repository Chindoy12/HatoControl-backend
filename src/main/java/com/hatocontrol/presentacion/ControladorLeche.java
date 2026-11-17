package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioLeche;
import com.hatocontrol.presentacion.dto.LecheRequest;
import com.hatocontrol.presentacion.dto.LecheResponse;
import com.hatocontrol.presentacion.dto.PromedioLecheResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leche")
public class ControladorLeche {

    private final ServicioLeche servicioLeche;

    public ControladorLeche(ServicioLeche servicioLeche) {
        this.servicioLeche = servicioLeche;
    }

    @PostMapping
    public LecheResponse registrar(@RequestBody LecheRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @GetMapping("/{codigoArete}/promedios")
    public PromedioLecheResponse promedios(@PathVariable String codigoArete, @RequestParam String periodo) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
