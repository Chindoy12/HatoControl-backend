package com.hatocontrol.presentacion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/** Rutas públicas, protección CSRF y codificador de contraseñas (clase técnica autorizada en la Fase 7). */
@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

    @Bean
    public SecurityFilterChain cadenaDeSeguridad(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .authorizeHttpRequests(rutas -> rutas
                .requestMatchers(FiltroAutenticacion.RUTAS_PUBLICAS.stream().map(r -> r + "/**").toArray(String[]::new)).permitAll()
                .requestMatchers(FiltroAutenticacion.RUTAS_PUBLICAS.toArray(String[]::new)).permitAll()
                .anyRequest().authenticated())
            .httpBasic(basico -> basico.disable())
            .formLogin(formulario -> formulario.disable());
        return http.build();
    }

    @Bean
    public PasswordEncoder codificadorDeContrasenas() {
        return new BCryptPasswordEncoder();
    }
}
