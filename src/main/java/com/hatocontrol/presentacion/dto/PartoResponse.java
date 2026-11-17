package com.hatocontrol.presentacion.dto;

import java.time.LocalDate;
import java.util.List;

public record PartoResponse(LocalDate fecha, List<AnimalResponse> crias) {
}
