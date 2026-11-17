package com.hatocontrol.presentacion.dto;

import java.time.LocalDate;
import java.util.List;

public record ReporteResponse(String tipo, LocalDate fechaGeneracion, List<String> filas) {
}
