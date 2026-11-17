package com.hatocontrol.presentacion.dto;

import com.hatocontrol.modelo.Rol;

public record UsuarioResponse(String nombre, String correo, Rol rol, boolean activo) {
}
