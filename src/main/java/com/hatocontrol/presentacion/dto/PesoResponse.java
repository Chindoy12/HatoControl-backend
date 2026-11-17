package com.hatocontrol.presentacion.dto;

import java.time.LocalDate;

public record PesoResponse(double pesoKg, LocalDate fechaMedicion, Double diferenciaKg) {
}
