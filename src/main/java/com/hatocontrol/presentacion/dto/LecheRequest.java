package com.hatocontrol.presentacion.dto;

import java.time.LocalDate;

public record LecheRequest(String codigoArete, LocalDate fechaParto, LocalDate fecha, double litros) {
}
