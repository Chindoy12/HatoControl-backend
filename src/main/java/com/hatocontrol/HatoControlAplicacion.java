package com.hatocontrol;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HatoControlAplicacion {

    public static void main(String[] args) {
        SpringApplication.run(HatoControlAplicacion.class, args);
    }

    @Bean
    public Clock reloj() {
        return Clock.system(ZoneId.of("America/Bogota"));
    }
}
