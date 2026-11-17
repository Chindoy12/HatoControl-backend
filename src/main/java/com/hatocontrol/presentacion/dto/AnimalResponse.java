package com.hatocontrol.presentacion.dto;

import com.hatocontrol.modelo.Proposito;
import com.hatocontrol.modelo.Sexo;

public record AnimalResponse(String codigoArete, String nombre, Sexo sexo, Integer edadMeses, Proposito proposito) {
}
