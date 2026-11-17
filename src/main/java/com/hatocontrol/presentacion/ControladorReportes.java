package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioReportes;
import com.hatocontrol.presentacion.dto.ReporteRequest;
import com.hatocontrol.presentacion.dto.ReporteResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ControladorReportes {

    private final ServicioReportes servicioReportes;

    public ControladorReportes(ServicioReportes servicioReportes) {
        this.servicioReportes = servicioReportes;
    }

    @GetMapping("/dashboard")
    public ReporteResponse dashboard() {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @PostMapping
    public ReporteResponse reporte(@RequestBody ReporteRequest solicitud) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
