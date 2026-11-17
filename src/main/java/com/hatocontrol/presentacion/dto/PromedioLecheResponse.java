package com.hatocontrol.presentacion.dto;

import java.util.List;

public record PromedioLecheResponse(String periodo, int diasConRegistro, double totalLitros, double promedioDiario, List<LecheResponse> registros) {
}
