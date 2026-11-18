package com.hatocontrol.presentacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

/** fechaParto es opcional: si no llega, se usa el parto más reciente de la vaca. */
public record LecheRequest(
        @NotBlank(message = "Selecciona una vaca.") String codigoArete,
        LocalDate fechaParto,
        @NotNull(message = "Escribe la fecha.") LocalDate fecha,
        @NotNull(message = "Escribe los litros.") @Positive(message = "Los litros deben ser mayores que cero.") Double litros) {
}
