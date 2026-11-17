package com.hatocontrol.presentacion.dto;

import java.time.LocalDate;
import java.util.List;

public record PartoRequest(String codigoAreteMadre, LocalDate fecha, List<AnimalRequest> crias) {
}
