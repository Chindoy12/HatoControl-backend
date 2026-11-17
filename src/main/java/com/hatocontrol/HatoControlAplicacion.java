package com.hatocontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de arranque de la API de HatoControl (clase técnica autorizada en la Fase 7). */
@SpringBootApplication
public class HatoControlAplicacion {

    public static void main(String[] args) {
        SpringApplication.run(HatoControlAplicacion.class, args);
    }
}
