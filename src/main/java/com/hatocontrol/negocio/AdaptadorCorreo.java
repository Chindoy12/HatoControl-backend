package com.hatocontrol.negocio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class AdaptadorCorreo implements IServicioCorreo {

    private final JavaMailSender enviador;
    private final String remitente;

    public AdaptadorCorreo(JavaMailSender enviador, @Value("${hatocontrol.correo-remitente}") String remitente) {
        this.enviador = enviador;
        this.remitente = remitente;
    }

    @Override
    public void enviarEnlace(String correo, String enlace) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(correo);
        mensaje.setSubject("HatoControl · Crea tu contraseña nueva");
        mensaje.setText("""
                Hola:

                Pediste crear una contraseña nueva para HatoControl. Abre este enlace en los próximos 30 minutos:

                %s

                Si no lo pediste, ignora este correo: tu contraseña no cambia.
                """.formatted(enlace));
        enviador.send(mensaje);
    }
}
