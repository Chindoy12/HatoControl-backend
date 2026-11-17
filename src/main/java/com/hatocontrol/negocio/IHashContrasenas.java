package com.hatocontrol.negocio;

public interface IHashContrasenas {

    String cifrar(String contrasena);

    boolean verificar(String contrasena, String hash);
}
