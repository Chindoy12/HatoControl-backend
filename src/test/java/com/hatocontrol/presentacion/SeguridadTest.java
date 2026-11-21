package com.hatocontrol.presentacion;

import static com.hatocontrol.presentacion.CriteriosAceptacionTest.animal;
import static com.hatocontrol.presentacion.CriteriosAceptacionTest.cuenta;
import static com.hatocontrol.presentacion.CriteriosAceptacionTest.esperar;
import static com.hatocontrol.presentacion.CriteriosAceptacionTest.ingreso;
import static com.hatocontrol.presentacion.CriteriosAceptacionTest.sinTexto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hatocontrol.negocio.IServicioCorreo;
import com.hatocontrol.presentacion.ClientePrueba.Respuesta;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Requisitos de seguridad: RNF19 (autenticación), RNF20 (control de acceso en el backend),
 * RNF22 (protección de la contraseña) y RNF23 (recuperación segura).
 * HTTPS, la cookie «Secure» y la base de datos privada (RNF21) se comprueban con la aplicación publicada (Fase 12).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.servlet.session.cookie.secure=false")
@Import(SeguridadTest.CorreoDePrueba.class)
@ExtendWith(OutputCaptureExtension.class)
class SeguridadTest {

    static final LocalDate HOY = LocalDate.now(ZoneId.of("America/Bogota"));

    /** Reemplaza el envío real del correo en las pruebas: guarda cada enlace para poder abrirlo. */
    @TestConfiguration
    static class CorreoDePrueba {

        private static final List<String[]> ENVIADOS = new CopyOnWriteArrayList<>();

        @Bean
        @Primary
        IServicioCorreo correoDePrueba() {
            return (correo, enlace) -> ENVIADOS.add(new String[] {correo, enlace});
        }

        static String ultimoEnlace(String correo) {
            String enlace = null;
            for (String[] e : ENVIADOS) {
                if (e[0].equals(correo)) {
                    enlace = e[1];
                }
            }
            assertTrue(enlace != null, "No se envió ningún enlace a " + correo);
            return enlace;
        }

        static String token(String correo) {
            String enlace = ultimoEnlace(correo);
            return enlace.substring(enlace.lastIndexOf('/') + 1);
        }

        static boolean recibio(String correo) {
            return ENVIADOS.stream().anyMatch(e -> e[0].equals(correo));
        }
    }

    @LocalServerPort
    int puerto;

    @Autowired
    DataSource fuenteDatos;

    private JdbcTemplate bd() {
        return new JdbcTemplate(fuenteDatos);
    }

    // ---------- RNF19 Autenticación ----------

    @Test
    @DisplayName("RNF19 · Sin iniciar sesión no se puede consultar ni modificar nada")
    void rnf19_sinSesionNoHayAcceso() {
        ClientePrueba anonimo = new ClientePrueba(puerto);
        String[] consultas = {"/api/sesion", "/api/animales", "/api/animales/CO-0142", "/api/animales/CO-0142/pesos",
            "/api/sanitario/animales/CO-0142", "/api/sanitario/pendientes", "/api/sanitario/proximas?dias=30",
            "/api/partos/madres/CO-0142", "/api/leche/CO-0142/promedios?periodo=MES", "/api/reportes/dashboard",
            "/api/cuentas/administradores"};
        for (String ruta : consultas) {
            esperar(401, anonimo.get(ruta), "Inicia sesión para continuar.");
        }
        esperar(401, anonimo.post("/api/animales", animal("ANON-1", "MACHO", null, "ENGORDE")));
        esperar(401, anonimo.post("/api/reportes", "{\"tipo\":\"ANIMALES\"}"));
        esperar(401, anonimo.delete("/api/animales/CO-0256"));
        assertEquals(0, bd().queryForObject("select count(*) from animales where codigo_arete = 'ANON-1'", Integer.class));
    }

    @Test
    @DisplayName("RNF19 · La contraseña tiene entre 10 y 12 caracteres en registro, administradores y recuperación")
    void rnf19_longitudDeLaContrasena() {
        ClientePrueba c = new ClientePrueba(puerto);
        esperar(400, c.post("/api/cuentas/fincas", cuenta("Nueve", "A", "nueve@prueba.example", "Clave1234")), "entre 10 y 12");
        esperar(400, c.post("/api/cuentas/fincas", cuenta("Trece", "A", "trece@prueba.example", "Clave12345678")), "entre 10 y 12");
        esperar(201, new ClientePrueba(puerto).post("/api/cuentas/fincas", cuenta("Diez", "A", "diez@prueba.example", "Clave12345")));
        ClientePrueba doce = new ClientePrueba(puerto);
        esperar(201, doce.post("/api/cuentas/fincas", cuenta("Doce", "A", "doce@prueba.example", "Clave1234567")));

        esperar(400, doce.post("/api/cuentas/administradores", cuenta(null, "B", "b9@prueba.example", "Clave1234")), "entre 10 y 12");
        esperar(400, doce.post("/api/cuentas/administradores", cuenta(null, "B", "b13@prueba.example", "Clave12345678")), "entre 10 y 12");
        esperar(201, doce.post("/api/cuentas/administradores", cuenta(null, "B", "b10@prueba.example", "Clave12345")));
        esperar(422, doce.post("/api/cuentas/administradores/contrasena", ingreso("b10@prueba.example", "Clave12345678")), "entre 10 y 12");

        esperar(200, c.post("/api/sesion/recuperacion", "{\"correo\":\"doce@prueba.example\"}"));
        String token = CorreoDePrueba.token("doce@prueba.example");
        esperar(422, c.post("/api/sesion/recuperacion/" + token, "{\"contrasena\":\"Clave12345678\"}"), "entre 10 y 12");
        esperar(204, c.post("/api/sesion/recuperacion/" + token, "{\"contrasena\":\"Clave7654321\"}"));
    }

    // ---------- RNF20 Control de acceso en el backend ----------

    @Test
    @DisplayName("RNF20 · Un usuario de otra finca no puede modificar datos de La Esperanza aunque conozca el arete")
    void rnf20_otraFincaNoModificaNada() {
        ClientePrueba marta = ClientePrueba.conSesion(puerto, CriteriosAceptacionTest.OTRA_FINCA);
        Long brucelosis = bd().queryForObject("""
                select r.id from registros_sanitarios r join animales a on a.id = r.animal_id
                where a.codigo_arete = 'CO-0142' and r.descripcion = 'Brucelosis'""", Long.class);
        int pesosAntes = bd().queryForObject("select count(*) from registros_peso", Integer.class);
        int lecheAntes = bd().queryForObject("select count(*) from registros_leche", Integer.class);

        Respuesta[] intentos = {
            marta.put("/api/animales/CO-0142", animal("CO-0142", "HEMBRA", null, "ENGORDE")),
            marta.delete("/api/animales/CO-0256"),
            marta.post("/api/animales/CO-0201/engorde", "{}"),
            marta.post("/api/animales/CO-0142/pesos", "{\"pesoKg\":999,\"fechaMedicion\":\"" + HOY + "\"}"),
            marta.post("/api/leche", "{\"codigoArete\":\"CO-0142\",\"fecha\":\"" + HOY + "\",\"litros\":99}"),
            marta.post("/api/sanitario/animales/CO-0142/eventos",
                    "{\"tipo\":\"VACUNA\",\"descripcion\":\"Intrusa\",\"fechaRealizacion\":\"" + HOY + "\"}"),
            marta.post("/api/partos", "{\"codigoAreteMadre\":\"CO-0142\",\"fecha\":\"" + HOY + "\",\"crias\":[]}"),
            marta.post("/api/sanitario/eventos/" + brucelosis + "/realizada?fechaRealizacion=" + HOY, "{}"),
        };
        for (Respuesta r : intentos) {
            assertTrue(r.estado() == 404 || r.estado() == 422, "Se esperaba 404 o 422: " + r.estado() + " " + r.cuerpo());
        }

        ClientePrueba jaider = ClientePrueba.conSesion(puerto, CriteriosAceptacionTest.GANADERO);
        esperar(200, jaider.get("/api/animales/CO-0142"), "\"proposito\":\"LECHE\"");
        esperar(200, jaider.get("/api/animales/CO-0256"));
        esperar(200, jaider.get("/api/animales/CO-0201"), "\"proposito\":\"PADRON\"");
        assertEquals("PROGRAMADA", bd().queryForObject("select estado from registros_sanitarios where id = ?", String.class, brucelosis));
        assertEquals(0, bd().queryForObject("select count(*) from registros_sanitarios where descripcion = 'Intrusa'", Integer.class));
        assertEquals(pesosAntes, bd().queryForObject("select count(*) from registros_peso", Integer.class));
        assertEquals(lecheAntes, bd().queryForObject("select count(*) from registros_leche", Integer.class));
    }

    @Test
    @DisplayName("RNF20 · Las solicitudes que modifican datos sin el token CSRF se rechazan")
    void rnf20_sinTokenCsrfSeRechaza() {
        ClientePrueba c = ClientePrueba.conSesion(puerto, CriteriosAceptacionTest.GANADERO);
        assertEquals(403, c.postSinCsrf("/api/animales", animal("CSRF-1", "MACHO", null, "ENGORDE")).estado());
        assertEquals(0, bd().queryForObject("select count(*) from animales where codigo_arete = 'CSRF-1'", Integer.class));
    }

    // ---------- RNF22 Protección de la contraseña ----------

    @Test
    @DisplayName("RNF22 · La contraseña se guarda solo como hash BCrypt y nunca aparece en las respuestas")
    void rnf22_contrasenaNuncaEnRespuestasYGuardadaComoHash() {
        ClientePrueba c = new ClientePrueba(puerto);
        Respuesta registro = c.post("/api/cuentas/fincas", cuenta("Hash", "Hugo", "hugo@prueba.example", "Secreta2026"));
        Respuesta admin = c.post("/api/cuentas/administradores", cuenta(null, "Hilda", "hilda@prueba.example", "Oculta2026"));
        Respuesta lista = c.get("/api/cuentas/administradores");
        Respuesta sesion = c.get("/api/sesion");
        Respuesta ingresoHilda = new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("hilda@prueba.example", "Oculta2026"));
        Respuesta ingresoMalo = new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("hilda@prueba.example", "Incorrecta26"));
        for (Respuesta r : new Respuesta[] {registro, admin, lista, sesion, ingresoHilda, ingresoMalo}) {
            sinTexto(r, "Secreta2026", "Oculta2026", "Incorrecta26", "$2a$", "$2b$", "hash", "contrasena");
        }
        for (String correo : new String[] {"hugo@prueba.example", "hilda@prueba.example"}) {
            String hash = bd().queryForObject("select hash_contrasena from usuarios where correo = ?", String.class, correo);
            assertTrue(hash.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$"), "No es un hash BCrypt: " + hash);
        }
        assertEquals(0, bd().queryForObject(
                "select count(*) from usuarios where hash_contrasena in ('Secreta2026', 'Oculta2026', 'Esperanza26')", Integer.class));
    }

    @Test
    @DisplayName("RNF22 · La sesión usa una cookie HttpOnly y SameSite que el código de la página no puede leer")
    void rnf22_cookieDeSesion() {
        Respuesta r = new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso(CriteriosAceptacionTest.GANADERO, "Esperanza26"));
        esperar(200, r);
        String cookie = r.encabezados().allValues("set-cookie").stream()
                .filter(v -> v.startsWith("JSESSIONID=")).findFirst().orElseThrow();
        assertTrue(cookie.contains("HttpOnly"), cookie);
        assertTrue(cookie.contains("SameSite=Lax"), cookie);
    }

    @Test
    @DisplayName("RNF22 · El ingreso no acepta la contraseña en la URL")
    void rnf22_contrasenaNoViajaEnLaUrl() {
        Respuesta r = new ClientePrueba(puerto).get("/api/sesion/ingreso?correo=" + CriteriosAceptacionTest.GANADERO + "&contrasena=Esperanza26");
        assertNotEquals(200, r.estado(), r.cuerpo());
        sinTexto(r, "\"rol\":\"GANADERO\"");
    }

    @Test
    @DisplayName("RNF22 · Ni la contraseña ni el enlace de recuperación quedan en los registros del servidor")
    void rnf22_nadaSensibleEnLosRegistros(CapturedOutput salida) {
        ClientePrueba c = new ClientePrueba(puerto);
        c.post("/api/cuentas/fincas", cuenta("Registros", "Rita", "rita@prueba.example", "Privada2026"));
        new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("rita@prueba.example", "Privada2026"));
        new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("rita@prueba.example", "Equivocada26"));
        c.post("/api/sesion/recuperacion", "{\"correo\":\"rita@prueba.example\"}");
        String token = CorreoDePrueba.token("rita@prueba.example");
        c.post("/api/sesion/recuperacion/" + token, "{\"contrasena\":\"Renovada2026\"}");
        assertFalse(salida.getAll().contains("Privada2026"));
        assertFalse(salida.getAll().contains("Equivocada26"));
        assertFalse(salida.getAll().contains("Renovada2026"));
        assertFalse(salida.getAll().contains(token));
    }

    // ---------- RNF23 Recuperación segura de contraseña ----------

    @Test
    @DisplayName("RNF23 · El enlace se guarda solo como hash y vence a los 30 minutos")
    void rnf23_enlaceGuardadoComoHashYVenceEn30Minutos() {
        new ClientePrueba(puerto).post("/api/cuentas/fincas", cuenta("Treinta", "Tomás", "tomas@prueba.example", "Treinta2026"));
        LocalDateTime antes = LocalDateTime.now();
        esperar(200, new ClientePrueba(puerto).post("/api/sesion/recuperacion", "{\"correo\":\"tomas@prueba.example\"}"),
                "Vence en 30 minutos.");
        String token = CorreoDePrueba.token("tomas@prueba.example");
        assertTrue(token.length() >= 40, "El token es demasiado corto: " + token);
        String hash = bd().queryForObject("""
                select t.hash_token from tokens_recuperacion t join usuarios u on u.id = t.usuario_id
                where u.correo = 'tomas@prueba.example'""", String.class);
        assertNotEquals(token, hash);
        assertEquals(0, bd().queryForObject("select count(*) from tokens_recuperacion where hash_token = ?", Integer.class, token));
        Timestamp vence = bd().queryForObject("""
                select t.vence_en from tokens_recuperacion t join usuarios u on u.id = t.usuario_id
                where u.correo = 'tomas@prueba.example'""", Timestamp.class);
        long minutos = Duration.between(antes, vence.toLocalDateTime()).toMinutes();
        assertTrue(minutos >= 29 && minutos <= 30, "El enlace vence en " + minutos + " minutos");
    }

    @Test
    @DisplayName("RNF23 · El enlace sirve una sola vez")
    void rnf23_enlaceDeUnSoloUso() {
        new ClientePrueba(puerto).post("/api/cuentas/fincas", cuenta("Una Vez", "Úrsula", "ursula@prueba.example", "UnaVez20266"));
        ClientePrueba c = new ClientePrueba(puerto);
        c.post("/api/sesion/recuperacion", "{\"correo\":\"ursula@prueba.example\"}");
        String token = CorreoDePrueba.token("ursula@prueba.example");
        esperar(204, c.post("/api/sesion/recuperacion/" + token, "{\"contrasena\":\"Primera2026\"}"));
        esperar(422, c.post("/api/sesion/recuperacion/" + token, "{\"contrasena\":\"Segunda2026\"}"), "Solicita un enlace nuevo.");
        esperar(200, new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("ursula@prueba.example", "Primera2026")));
        assertTrue(bd().queryForObject("""
                select bool_and(t.usado) from tokens_recuperacion t join usuarios u on u.id = t.usuario_id
                where u.correo = 'ursula@prueba.example'""", Boolean.class));
    }
}
