package com.hatocontrol.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hatocontrol.presentacion.ClientePrueba.Respuesta;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Prueba la API completa, como la usa la aplicación web, contra la base con los datos de prueba de HatoControl-db.
 * Cada prueba indica la historia de usuario y el criterio de aceptación que comprueba.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.servlet.session.cookie.secure=false")
class CriteriosAceptacionTest {

    static final String GANADERO = "jaider@laesperanza.example";
    static final String ADMIN = "camila@laesperanza.example";
    static final String OTRA_FINCA = "marta@losrosales.example";
    static final LocalDate HOY = LocalDate.now(ZoneId.of("America/Bogota"));

    @LocalServerPort
    int puerto;

    private ClientePrueba ganadero() {
        return ClientePrueba.conSesion(puerto, GANADERO);
    }

    private static void esperar(int estado, Respuesta r, String... textos) {
        assertEquals(estado, r.estado(), r.cuerpo());
        for (String t : textos) {
            assertTrue(r.cuerpo().contains(t), "Se esperaba «" + t + "» en: " + r.cuerpo());
        }
    }

    private static String animal(String arete, String sexo, String fecha, String proposito) {
        return "{\"codigoArete\":\"" + arete + "\",\"nombre\":\"Prueba\",\"sexo\":\"" + sexo + "\",\"fechaNacimiento\":"
                + (fecha == null ? "null" : "\"" + fecha + "\"") + ",\"proposito\":\"" + proposito + "\"}";
    }

    // ---------- HU_021 y HU_022: sesión, permisos y fincas ----------

    @Test
    void hu021_ingresarConsultarSesionYSalir() {
        ClientePrueba c = ganadero();
        esperar(200, c.get("/api/sesion"), "\"rol\":\"GANADERO\"", "La Esperanza");
        esperar(204, c.post("/api/sesion/salida", "{}"));
        esperar(401, c.get("/api/animales"), "Inicia sesión");
    }

    @Test
    void hu021_datosIncorrectosSinDecirCualFallo() {
        ClientePrueba c = new ClientePrueba(puerto);
        esperar(403, c.post("/api/sesion/ingreso", "{\"correo\":\"" + GANADERO + "\",\"contrasena\":\"otraClave12\"}"),
                "El correo o la contraseña no son correctos.");
        esperar(403, c.post("/api/sesion/ingreso", "{\"correo\":\"nadie@x.example\",\"contrasena\":\"otraClave12\"}"),
                "El correo o la contraseña no son correctos.");
    }

    @Test
    void rnf_solicitudSinTokenCsrfSeRechaza() {
        ClientePrueba c = ganadero();
        assertEquals(403, c.postSinCsrf("/api/animales", animal("CSRF-1", "MACHO", null, "ENGORDE")).estado());
    }

    @Test
    void hu022_administradorNoUsaDashboardNiAdministradoresPeroSiAnimales() {
        ClientePrueba admin = ClientePrueba.conSesion(puerto, ADMIN);
        esperar(403, admin.get("/api/reportes/dashboard"), "solo del ganadero");
        esperar(403, admin.post("/api/cuentas/administradores",
                "{\"nombre\":\"Intruso\",\"correo\":\"intruso@x.example\",\"contrasena\":\"Clave123456\"}"));
        esperar(201, admin.post("/api/animales", animal("ADM-1", "HEMBRA", null, "ENGORDE")));
        esperar(404, ganadero().get("/api/animales/INTRUSO"));
    }

    @Test
    void hu022_cadaUsuarioVeSoloSuFinca() {
        ClientePrueba marta = ClientePrueba.conSesion(puerto, OTRA_FINCA);
        Respuesta lista = marta.get("/api/animales");
        esperar(200, lista, "RS-0001");
        assertFalse(lista.cuerpo().contains("CO-0142"));
        esperar(404, marta.get("/api/animales/CO-0142"), "no está registrado");
        esperar(404, marta.get("/api/sanitario/animales/CO-0142"));
    }

    // ---------- HU_001 a HU_005, HU_013 y HU_015: animales ----------

    @Test
    void hu001_reglasDeRegistro() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-1001", "HEMBRA", HOY.minusYears(1).toString(), "LECHE")), "T-1001");
        esperar(422, c.post("/api/animales", animal("t-1001", "HEMBRA", null, "LECHE")), "ya está registrado en la finca");
        esperar(422, c.post("/api/animales", animal("T-1002", "MACHO", null, "LECHE")), "Un macho solo puede ser de engorde o padrón.");
        esperar(422, c.post("/api/animales", animal("T-1003", "MACHO", HOY.plusDays(1).toString(), "ENGORDE")), "no puede ser posterior a hoy");
        esperar(400, c.post("/api/animales", "{\"sexo\":\"MACHO\",\"proposito\":\"ENGORDE\"}"), "\"codigoArete\":\"Escribe el código de arete.\"");
        // El mismo arete en otra finca sí se acepta
        esperar(201, ClientePrueba.conSesion(puerto, OTRA_FINCA).post("/api/animales", animal("T-1001", "MACHO", null, "PADRON")));
    }

    @Test
    void hu002_filtrarYBuscar() {
        ClientePrueba c = ganadero();
        Respuesta machos = c.get("/api/animales?sexo=MACHO");
        esperar(200, machos, "CO-0201");
        assertFalse(machos.cuerpo().contains("\"sexo\":\"HEMBRA\""));
        esperar(200, c.get("/api/animales?filtro=0142"), "Lucero");
        esperar(200, c.get("/api/animales?filtro=NO-EXISTE"));
        assertEquals("[]", c.get("/api/animales?filtro=NO-EXISTE").cuerpo());
    }

    @Test
    void hu003_fichaConEdadYSinFecha() {
        ClientePrueba c = ganadero();
        esperar(200, c.get("/api/animales/CO-0142"), "\"fechaNacimiento\":\"2021-03-12\"", "\"edadMeses\":");
        esperar(200, c.get("/api/animales/CO-0256"), "\"edadMeses\":null");
        esperar(404, c.get("/api/animales/CO-9999"), "El animal CO-9999 no está registrado en la finca.");
    }

    @Test
    void hu004_actualizarConservaAreteSiEstaOcupado() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-4001", "MACHO", null, "ENGORDE")));
        esperar(422, c.put("/api/animales/T-4001", animal("CO-0142", "MACHO", null, "ENGORDE")), "Se conserva el arete T-4001");
        esperar(422, c.put("/api/animales/T-4001", animal("T-4001", "HEMBRA", null, "PADRON")), "Elige de nuevo el propósito");
        esperar(200, c.put("/api/animales/T-4001", animal("T-4002", "MACHO", "2025-01-01", "ENGORDE")), "\"codigoArete\":\"T-4002\"");
    }

    @Test
    void hu005_eliminarSoloSinHistorial() {
        ClientePrueba c = ganadero();
        esperar(422, c.delete("/api/animales/CO-0142"), "registros sanitarios", "partos y crías", "registros de leche");
        esperar(201, c.post("/api/animales", animal("T-5001", "MACHO", null, "ENGORDE")));
        esperar(204, c.delete("/api/animales/T-5001"));
        esperar(404, c.get("/api/animales/T-5001"));
    }

    @Test
    void hu015_pasarAEngordeYNoRegistrarMasLeche() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-1501", "HEMBRA", "2022-01-01", "LECHE")));
        esperar(201, c.post("/api/partos", "{\"codigoAreteMadre\":\"T-1501\",\"fecha\":\"" + HOY.minusDays(10) + "\",\"crias\":[]}"));
        esperar(200, c.post("/api/animales/T-1501/engorde", "{}"), "\"proposito\":\"ENGORDE\"");
        esperar(422, c.post("/api/animales/T-1501/engorde", "{}"), "ya es de engorde");
        esperar(422, c.post("/api/leche", "{\"codigoArete\":\"T-1501\",\"fecha\":\"" + HOY + "\",\"litros\":10}"), "no leche");
    }

    // ---------- HU_006 a HU_009: sanidad ----------

    @Test
    void hu006_hu007_hu008_eventosYActividades() {
        ClientePrueba c = ganadero();
        String vacuna = "{\"tipo\":\"VACUNA\",\"descripcion\":\"Rabia\",\"fechaRealizacion\":\"" + HOY + "\"}";
        esperar(422, c.post("/api/sanitario/animales/NO-EXISTE/eventos", vacuna), "Debes seleccionar un animal registrado.");
        esperar(201, c.post("/api/sanitario/animales/CO-0163/eventos", vacuna), "\"estado\":\"REALIZADA\"");

        String ayer = "{\"tipo\":\"TRATAMIENTO\",\"descripcion\":\"Vitaminas\",\"fechaProgramada\":\"" + HOY.minusDays(1) + "\"}";
        esperar(422, c.post("/api/sanitario/animales/CO-0163/programadas", ayer), "no puede ser anterior a hoy");
        String manana = "{\"tipo\":\"TRATAMIENTO\",\"descripcion\":\"Vitaminas\",\"fechaProgramada\":\"" + HOY.plusDays(1) + "\"}";
        Respuesta programada = c.post("/api/sanitario/animales/CO-0163/programadas", manana);
        esperar(201, programada, "\"estado\":\"PROGRAMADA\"");

        // Las programadas no aparecen en el historial
        assertFalse(c.get("/api/sanitario/animales/CO-0163").cuerpo().contains("Vitaminas"));
        esperar(200, c.get("/api/sanitario/proximas?dias=2"), "Vitaminas");

        Matcher id = Pattern.compile("\"id\":(\\d+)").matcher(programada.cuerpo());
        assertTrue(id.find());
        esperar(200, c.post("/api/sanitario/eventos/" + id.group(1) + "/realizada?fechaRealizacion=" + HOY, "{}"),
                "\"estado\":\"REALIZADA\"", "\"fechaProgramada\":\"" + HOY.plusDays(1) + "\"");
        esperar(200, c.get("/api/sanitario/animales/CO-0163?tipo=TRATAMIENTO"), "Vitaminas");
        assertFalse(c.get("/api/sanitario/animales/CO-0163?tipo=TRATAMIENTO").cuerpo().contains("Rabia"));
    }

    @Test
    void hu009_pendientesVencidas() {
        esperar(200, ganadero().get("/api/sanitario/pendientes"), "CO-0188", "CO-0240");
    }

    // ---------- HU_010 y HU_011: peso ----------

    @Test
    void hu010_historialConVariacionesYValidaciones() {
        ClientePrueba c = ganadero();
        esperar(200, c.get("/api/animales/CO-0231/pesos"), "\"diferenciaKg\":null", "\"diferenciaKg\":24.0", "\"diferenciaKg\":-3.0");
        esperar(400, c.post("/api/animales/CO-0212/pesos", "{\"pesoKg\":0,\"fechaMedicion\":\"" + HOY + "\"}"), "El peso debe ser mayor que cero.");
        esperar(422, c.post("/api/animales/CO-0212/pesos", "{\"pesoKg\":350,\"fechaMedicion\":\"" + HOY.plusDays(1) + "\"}"), "posterior a hoy");
        esperar(201, c.post("/api/animales/CO-0212/pesos", "{\"pesoKg\":350,\"fechaMedicion\":\"" + HOY + "\"}"), "\"diferenciaKg\":9.0");
        assertEquals("[]", c.get("/api/animales/CO-0256/pesos").cuerpo());
    }

    // ---------- HU_016 a HU_019: partos y leche ----------

    @Test
    void hu016_hu017_partoConDosCriasYValidaciones() {
        ClientePrueba c = ganadero();
        esperar(422, c.post("/api/partos", "{\"codigoAreteMadre\":\"CO-0201\",\"fecha\":\"" + HOY + "\",\"crias\":[]}"),
                "Selecciona una hembra registrada.");
        esperar(422, c.post("/api/partos", "{\"codigoAreteMadre\":\"CO-0174\",\"fecha\":\"" + HOY + "\",\"crias\":["
                + animal("CO-0142", "HEMBRA", null, "LECHE") + "]}"), "ya está registrado");
        esperar(201, c.post("/api/partos", "{\"codigoAreteMadre\":\"CO-0174\",\"fecha\":\"" + HOY + "\",\"crias\":["
                + animal("T-1601", "HEMBRA", null, "LECHE") + "," + animal("T-1602", "MACHO", null, "PADRON") + "]}"), "T-1601", "T-1602");
        esperar(200, c.get("/api/animales/T-1601"), "\"fechaNacimiento\":\"" + HOY + "\"");
        esperar(200, c.get("/api/partos/madres/CO-0174"), "T-1601", "T-1602");
        assertEquals("[]", c.get("/api/partos/madres/CO-0240").cuerpo());
    }

    @Test
    void hu018_hu019_lecheYPromedios() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-1801", "HEMBRA", "2021-01-01", "LECHE")));
        esperar(422, c.post("/api/leche", "{\"codigoArete\":\"T-1801\",\"fecha\":\"" + HOY + "\",\"litros\":10}"), "no tiene partos registrados");
        LocalDate parto = HOY.minusDays(20);
        esperar(201, c.post("/api/partos", "{\"codigoAreteMadre\":\"T-1801\",\"fecha\":\"" + parto + "\",\"crias\":[]}"));
        esperar(422, c.post("/api/leche", "{\"codigoArete\":\"T-1801\",\"fecha\":\"" + parto.minusDays(1) + "\",\"litros\":10}"),
                "anterior al parto");
        esperar(400, c.post("/api/leche", "{\"codigoArete\":\"T-1801\",\"fecha\":\"" + HOY + "\",\"litros\":0}"), "mayores que cero");
        esperar(422, c.post("/api/leche", "{\"codigoArete\":\"CO-0240\",\"fecha\":\"" + HOY + "\",\"litros\":10}"), "no leche");
        LocalDate lunes = HOY.minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        for (int i = 0; i < 3; i++) {
            esperar(201, c.post("/api/leche", "{\"codigoArete\":\"T-1801\",\"fecha\":\"" + lunes.plusDays(i * 2) + "\",\"litros\":" + (18 + 2 * i) + "}"));
        }
        esperar(422, c.post("/api/leche", "{\"codigoArete\":\"T-1801\",\"fecha\":\"" + lunes + "\",\"litros\":10}"), "ya está registrada");
        // Semana con días sin registro: 18, 20 y 22 → 20 litros por día
        esperar(200, c.get("/api/leche/T-1801/promedios?periodo=SEMANA&fecha=" + lunes), "\"diasConRegistro\":3", "\"promedioDiario\":20.0");
        esperar(200, c.get("/api/leche/T-1801/promedios?periodo=MES&fecha=" + parto.minusMonths(2)), "\"diasConRegistro\":0");
    }

    // ---------- HU_012 y HU_014: dashboard y reportes ----------

    @Test
    void hu012_hu014_dashboardYReportes() {
        ClientePrueba c = ganadero();
        esperar(200, c.get("/api/reportes/dashboard"), "resumen\\tanimales", "actividad\\tVENCIDA", "peso\\tCO-0231");
        esperar(200, c.post("/api/reportes", "{\"tipo\":\"PESO\",\"codigoArete\":\"CO-0231\"}"), "286");
        esperar(200, c.post("/api/reportes", "{\"tipo\":\"SANITARIO\",\"codigoArete\":\"CO-0256\"}"), "\"filas\":[]");
    }

    // ---------- HU_020, HU_023 y HU_024: cuentas ----------

    @Test
    void hu020_registrarFincaYEntrar() {
        ClientePrueba c = new ClientePrueba(puerto);
        String cuenta = "{\"nombreFinca\":\"El Paraíso\",\"nombre\":\"Rosa\",\"correo\":\"rosa@elparaiso.example\",\"contrasena\":\"Paraiso2026\"}";
        esperar(201, c.post("/api/cuentas/fincas", cuenta), "El Paraíso");
        esperar(200, c.get("/api/sesion"), "Rosa");
        esperar(422, new ClientePrueba(puerto).post("/api/cuentas/fincas", cuenta), "El correo ya está registrado.");
        esperar(400, new ClientePrueba(puerto).post("/api/cuentas/fincas",
                "{\"nombreFinca\":\"X\",\"nombre\":\"X\",\"correo\":\"x@x.example\",\"contrasena\":\"corta\"}"), "entre 10 y 12");
    }

    @Test
    void hu023_crearDesactivarYRestablecerAdministrador() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/cuentas/administradores",
                "{\"nombre\":\"Sofía\",\"correo\":\"sofia@laesperanza.example\",\"contrasena\":\"Inicial2026\"}"), "\"activo\":true");
        esperar(422, c.post("/api/cuentas/administradores",
                "{\"nombre\":\"Otra\",\"correo\":\"camila@laesperanza.example\",\"contrasena\":\"Inicial2026\"}"), "ya está registrado");
        esperar(204, c.post("/api/cuentas/administradores/contrasena", "{\"correo\":\"sofia@laesperanza.example\",\"contrasena\":\"Nueva123456\"}"));
        ClientePrueba sofia = new ClientePrueba(puerto);
        esperar(200, sofia.post("/api/sesion/ingreso", "{\"correo\":\"sofia@laesperanza.example\",\"contrasena\":\"Nueva123456\"}"));
        esperar(204, c.post("/api/cuentas/administradores/desactivacion", "{\"correo\":\"sofia@laesperanza.example\"}"));
        esperar(403, new ClientePrueba(puerto).post("/api/sesion/ingreso",
                "{\"correo\":\"sofia@laesperanza.example\",\"contrasena\":\"Nueva123456\"}"), "desactivada");
        esperar(200, c.get("/api/cuentas/administradores"), "\"activo\":false");
    }

    @Test
    void hu024_mismoMensajeYEnlaceInvalido() {
        ClientePrueba c = new ClientePrueba(puerto);
        Respuesta valida = c.post("/api/sesion/recuperacion", "{\"correo\":\"" + GANADERO + "\"}");
        Respuesta otra = c.post("/api/sesion/recuperacion", "{\"correo\":\"" + ADMIN + "\"}");
        esperar(200, valida);
        assertEquals(valida.cuerpo(), otra.cuerpo());
        esperar(422, c.post("/api/sesion/recuperacion/token-falso", "{\"contrasena\":\"Nueva123456\"}"), "Solicita un enlace nuevo.");
    }
}
