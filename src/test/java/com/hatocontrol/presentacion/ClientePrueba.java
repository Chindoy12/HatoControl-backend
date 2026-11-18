package com.hatocontrol.presentacion;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** Navegador mínimo para las pruebas: guarda las cookies de sesión y envía el token CSRF como la aplicación web. */
class ClientePrueba {

    record Respuesta(int estado, String cuerpo) {
    }

    private final String base;
    private final CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    private final HttpClient http;

    ClientePrueba(int puerto) {
        this.base = "http://localhost:" + puerto;
        this.http = HttpClient.newBuilder().cookieHandler(cookies).build();
        get("/api/sesion");
    }

    static ClientePrueba conSesion(int puerto, String correo) {
        ClientePrueba cliente = new ClientePrueba(puerto);
        Respuesta r = cliente.post("/api/sesion/ingreso", "{\"correo\":\"" + correo + "\",\"contrasena\":\"Esperanza26\"}");
        if (r.estado() != 200) {
            throw new IllegalStateException("No se pudo iniciar sesión: " + r);
        }
        return cliente;
    }

    Respuesta get(String ruta) {
        return enviar(HttpRequest.newBuilder(URI.create(base + ruta)).GET(), true);
    }

    Respuesta post(String ruta, String json) {
        return enviar(conCuerpo(ruta).POST(HttpRequest.BodyPublishers.ofString(json)), true);
    }

    Respuesta postSinCsrf(String ruta, String json) {
        return enviar(conCuerpo(ruta).POST(HttpRequest.BodyPublishers.ofString(json)), false);
    }

    Respuesta put(String ruta, String json) {
        return enviar(conCuerpo(ruta).PUT(HttpRequest.BodyPublishers.ofString(json)), true);
    }

    Respuesta delete(String ruta) {
        return enviar(HttpRequest.newBuilder(URI.create(base + ruta)).DELETE(), true);
    }

    private HttpRequest.Builder conCuerpo(String ruta) {
        return HttpRequest.newBuilder(URI.create(base + ruta)).header("Content-Type", "application/json");
    }

    private Respuesta enviar(HttpRequest.Builder peticion, boolean conCsrf) {
        if (conCsrf) {
            cookies.getCookieStore().getCookies().stream()
                    .filter(c -> c.getName().equals("XSRF-TOKEN"))
                    .findFirst()
                    .ifPresent(c -> peticion.header("X-XSRF-TOKEN", c.getValue()));
        }
        try {
            HttpResponse<String> r = http.send(peticion.build(), HttpResponse.BodyHandlers.ofString());
            return new Respuesta(r.statusCode(), r.body());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
