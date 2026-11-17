package com.hatocontrol.presentacion.dto;

import com.hatocontrol.modelo.TipoEventoSanitario;
import java.time.LocalDate;

public record EventoSanitarioRequest(TipoEventoSanitario tipo, String descripcion, LocalDate fechaProgramada, LocalDate fechaRealizacion, String observaciones) {
}
