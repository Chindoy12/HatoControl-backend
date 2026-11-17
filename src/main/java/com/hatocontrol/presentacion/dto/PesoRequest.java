package com.hatocontrol.presentacion.dto;

import java.time.LocalDate;

public record PesoRequest(double pesoKg, LocalDate fechaMedicion) {
}
