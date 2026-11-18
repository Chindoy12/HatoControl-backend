package com.hatocontrol.presentacion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record PartoRequest(
        @NotBlank(message = "Selecciona una hembra registrada.") String codigoAreteMadre,
        @NotNull(message = "Escribe la fecha del parto.") LocalDate fecha,
        List<@Valid AnimalRequest> crias) {
}
