package com.hatocontrol.presentacion;

import com.hatocontrol.negocio.ServicioLeche;
import com.hatocontrol.presentacion.dto.LecheRequest;
import com.hatocontrol.presentacion.dto.LecheResponse;
import com.hatocontrol.presentacion.dto.PromedioLecheResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leche")
public class ControladorLeche {

    private final ServicioLeche servicioLeche;
    private final FiltroAutenticacion filtro;

    public ControladorLeche(ServicioLeche servicioLeche, FiltroAutenticacion filtro) {
        this.servicioLeche = servicioLeche;
        this.filtro = filtro;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LecheResponse registrar(@Valid @RequestBody LecheRequest solicitud, HttpServletRequest peticion) {
        return servicioLeche.registrar(filtro.obtenerFinca(peticion), solicitud);
    }

    /** periodo: SEMANA, MES o PARTO. fecha (opcional): un día dentro del periodo a consultar. */
    @GetMapping("/{codigoArete}/promedios")
    public PromedioLecheResponse promedios(@PathVariable String codigoArete, @RequestParam String periodo,
            @RequestParam(required = false) LocalDate fecha, HttpServletRequest peticion) {
        return servicioLeche.promedio(filtro.obtenerFinca(peticion), codigoArete, periodo, fecha);
    }
}
