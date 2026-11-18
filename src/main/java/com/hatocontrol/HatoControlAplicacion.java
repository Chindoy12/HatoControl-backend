package com.hatocontrol;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/** Punto de arranque de la API de HatoControl (clase técnica autorizada en la Fase 7). */
@SpringBootApplication
public class HatoControlAplicacion {

    public static void main(String[] args) {
        SpringApplication.run(HatoControlAplicacion.class, args);
    }

    /** «Hoy» es el día en Colombia, aunque el servidor de AWS use otra zona horaria. */
    @Bean
    public Clock reloj() {
        return Clock.system(ZoneId.of("America/Bogota"));
    }
}
