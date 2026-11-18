package com.hatocontrol.presentacion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record PesoRequest(
        @NotNull(message = "Escribe el peso.") @Positive(message = "El peso debe ser mayor que cero.") Double pesoKg,
        @NotNull(message = "Escribe la fecha de medición.") LocalDate fechaMedicion) {
}
