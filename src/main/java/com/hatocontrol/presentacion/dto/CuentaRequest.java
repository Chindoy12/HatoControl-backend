package com.hatocontrol.presentacion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CuentaRequest(
        String nombreFinca,
        @NotBlank(message = "Escribe el nombre.") String nombre,
        @NotBlank(message = "Escribe el correo.") @Email(message = "Escribe un correo válido.") String correo,
        @NotBlank(message = "Escribe la contraseña.")
        @Size(min = 10, max = 12, message = "La contraseña debe tener entre 10 y 12 caracteres.") String contrasena) {
}
