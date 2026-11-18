package com.hatocontrol.presentacion.dto;

import com.hatocontrol.modelo.EstadoActividad;
import com.hatocontrol.modelo.TipoEventoSanitario;
import java.time.LocalDate;

public record EventoSanitarioResponse(Long id, String codigoArete, String nombreAnimal, TipoEventoSanitario tipo, String descripcion,
        LocalDate fechaProgramada, LocalDate fechaRealizacion, EstadoActividad estado, String observaciones) {
}
