package com.hatocontrol.negocio;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Implementa IHashContrasenas con el PasswordEncoder de Spring Security (clase técnica autorizada en la Fase 7). */
@Component
public class HashContrasenasSpring implements IHashContrasenas {

    private final PasswordEncoder codificador;

    public HashContrasenasSpring(PasswordEncoder codificador) {
        this.codificador = codificador;
    }

    @Override
    public String cifrar(String contrasena) {
        return codificador.encode(contrasena);
    }

    @Override
    public boolean verificar(String contrasena, String hash) {
        return codificador.matches(contrasena, hash);
    }
}
