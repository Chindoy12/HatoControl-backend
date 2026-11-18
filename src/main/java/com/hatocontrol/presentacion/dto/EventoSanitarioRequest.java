package com.hatocontrol.presentacion.dto;

import com.hatocontrol.modelo.TipoEventoSanitario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EventoSanitarioRequest(
        @NotNull(message = "Elige el tipo de evento.") TipoEventoSanitario tipo,
        @NotBlank(message = "Escribe la descripción.") String descripcion,
        LocalDate fechaProgramada,
        LocalDate fechaRealizacion,
        String observaciones) {
}
