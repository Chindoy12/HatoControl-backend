package com.hatocontrol.presentacion.dto;

import com.hatocontrol.modelo.Proposito;
import com.hatocontrol.modelo.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AnimalRequest(
        @NotBlank(message = "Escribe el código de arete.") String codigoArete,
        String nombre,
        @NotNull(message = "Elige el sexo.") Sexo sexo,
        LocalDate fechaNacimiento,
        @NotNull(message = "Elige el propósito.") Proposito proposito) {
}
