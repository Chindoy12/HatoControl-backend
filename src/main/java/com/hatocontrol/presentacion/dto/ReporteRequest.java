package com.hatocontrol.presentacion.dto;

import jakarta.validation.constraints.NotBlank;

public record ReporteRequest(
        @NotBlank(message = "Elige el tipo de reporte.") String tipo,
        String codigoArete) {
}
