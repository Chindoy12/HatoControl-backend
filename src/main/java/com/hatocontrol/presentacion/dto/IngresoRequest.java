package com.hatocontrol.presentacion.dto;

import jakarta.validation.constraints.NotBlank;

public record IngresoRequest(
        @NotBlank(message = "Escribe tu correo.") String correo,
        @NotBlank(message = "Escribe tu contraseña.") String contrasena) {
}
