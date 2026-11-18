package com.hatocontrol.presentacion.dto;

import com.hatocontrol.modelo.Proposito;
import com.hatocontrol.modelo.Sexo;
import java.time.LocalDate;

public record AnimalResponse(String codigoArete, String nombre, Sexo sexo, LocalDate fechaNacimiento, Integer edadMeses, Proposito proposito) {
}
