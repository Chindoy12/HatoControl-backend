package com.hatocontrol.negocio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class HashContrasenasSpringTest {

    private final IHashContrasenas hash = new HashContrasenasSpring(new BCryptPasswordEncoder());

    @Test
    void guardaSoloElHashYLoVerifica() {
        String cifrada = hash.cifrar("Esperanza26");
        assertNotEquals("Esperanza26", cifrada);
        assertTrue(hash.verificar("Esperanza26", cifrada));
        assertFalse(hash.verificar("otraClave12", cifrada));
    }
}
