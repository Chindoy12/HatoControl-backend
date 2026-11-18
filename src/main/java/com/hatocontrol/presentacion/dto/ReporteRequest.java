package com.hatocontrol.presentacion.dto;

import jakarta.validation.constraints.NotBlank;

/** tipo: ANIMALES, SANITARIO o PESO. codigoArete solo para SANITARIO y PESO. */
public record ReporteRequest(
        @NotBlank(message = "Elige el tipo de reporte.") String tipo,
        String codigoArete) {
}
