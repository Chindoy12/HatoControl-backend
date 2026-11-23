package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioReportes;
import com.hatocontrol.presentacion.dto.ReporteRequest;
import com.hatocontrol.presentacion.dto.ReporteResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ControladorReportes {

    private final ServicioReportes servicioReportes;
    private final FiltroAutenticacion filtro;

    public ControladorReportes(ServicioReportes servicioReportes, FiltroAutenticacion filtro) {
        this.servicioReportes = servicioReportes;
        this.filtro = filtro;
    }

    @GetMapping("/dashboard")
    public ReporteResponse dashboard(HttpServletRequest peticion) {
        return servicioReportes.dashboard(filtro.obtenerFinca(peticion));
    }

    @PostMapping
    public ReporteResponse reporte(@Valid @RequestBody ReporteRequest solicitud, HttpServletRequest peticion) {
        return servicioReportes.reporte(filtro.obtenerFinca(peticion), solicitud);
    }
}
