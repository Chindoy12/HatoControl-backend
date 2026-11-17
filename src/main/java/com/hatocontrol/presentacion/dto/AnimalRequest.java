package com.hatocontrol.presentacion.dto;

import com.hatocontrol.modelo.Proposito;
import com.hatocontrol.modelo.Sexo;
import java.time.LocalDate;

public record AnimalRequest(String codigoArete, String nombre, Sexo sexo, LocalDate fechaNacimiento, Proposito proposito) {
}
