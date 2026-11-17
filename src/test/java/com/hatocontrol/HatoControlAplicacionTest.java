package com.hatocontrol;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Arranca la API completa contra la base creada por HatoControl-db.
 * Con ddl-auto=validate, falla si alguna entidad no coincide con su tabla.
 */
@SpringBootTest
class HatoControlAplicacionTest {

    @Test
    void lasEntidadesCoincidenConLasTablas() {
    }
}
