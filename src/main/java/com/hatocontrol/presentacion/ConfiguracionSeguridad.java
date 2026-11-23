package com.hatocontrol.presentacion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

    @Bean
    public SecurityFilterChain cadenaDeSeguridad(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.spa())
            .authorizeHttpRequests(rutas -> rutas.anyRequest().permitAll())
            .httpBasic(basico -> basico.disable())
            .formLogin(formulario -> formulario.disable())
            .logout(salida -> salida.disable());
        return http.build();
    }

    @Bean
    public PasswordEncoder codificadorDeContrasenas() {
        return new BCryptPasswordEncoder();
    }
}
