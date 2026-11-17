package com.hatocontrol.presentacion.dto;

import com.hatocontrol.modelo.Rol;

public record SesionResponse(String nombre, Rol rol, String nombreFinca) {
}
