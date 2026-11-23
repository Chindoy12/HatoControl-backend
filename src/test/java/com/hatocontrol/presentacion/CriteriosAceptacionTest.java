package com.hatocontrol.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hatocontrol.presentacion.ClientePrueba.Respuesta;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.servlet.session.cookie.secure=false")
@Import(SeguridadTest.CorreoDePrueba.class)
class CriteriosAceptacionTest {

    static final String GANADERO = "jaider@laesperanza.example";
    static final String ADMIN = "camila@laesperanza.example";
    static final String OTRA_FINCA = "marta@losrosales.example";
    static final LocalDate HOY = LocalDate.now(ZoneId.of("America/Bogota"));

    @LocalServerPort
    int puerto;

    @Autowired
    DataSource fuenteDatos;

    private ClientePrueba ganadero() {
        return ClientePrueba.conSesion(puerto, GANADERO);
    }

    private ClientePrueba administrador() {
        return ClientePrueba.conSesion(puerto, ADMIN);
    }

    private JdbcTemplate bd() {
        return new JdbcTemplate(fuenteDatos);
    }

    static void esperar(int estado, Respuesta r, String... textos) {
        assertEquals(estado, r.estado(), r.cuerpo());
        for (String t : textos) {
            assertTrue(r.cuerpo().contains(t), "Se esperaba «" + t + "» en: " + r.cuerpo());
        }
    }

    static void sinTexto(Respuesta r, String... textos) {
        for (String t : textos) {
            assertFalse(r.cuerpo().contains(t), "No se esperaba «" + t + "» en: " + r.cuerpo());
        }
    }

    static String animal(String arete, String sexo, String fecha, String proposito) {
        return "{\"codigoArete\":\"" + arete + "\",\"nombre\":\"Prueba\",\"sexo\":\"" + sexo + "\",\"fechaNacimiento\":"
                + (fecha == null ? "null" : "\"" + fecha + "\"") + ",\"proposito\":\"" + proposito + "\"}";
    }

    static String evento(String tipo, String descripcion, LocalDate fecha) {
        return "{\"tipo\":\"" + tipo + "\",\"descripcion\":\"" + descripcion + "\",\"fechaRealizacion\":\"" + fecha
                + "\",\"observaciones\":\"Prueba\"}";
    }

    static String programada(String tipo, String descripcion, LocalDate fecha) {
        return "{\"tipo\":\"" + tipo + "\",\"descripcion\":\"" + descripcion + "\",\"fechaProgramada\":\"" + fecha + "\"}";
    }

    static String peso(double kg, LocalDate fecha) {
        return "{\"pesoKg\":" + kg + ",\"fechaMedicion\":\"" + fecha + "\"}";
    }

    static String parto(String madre, LocalDate fecha, String... crias) {
        return "{\"codigoAreteMadre\":\"" + madre + "\",\"fecha\":\"" + fecha + "\",\"crias\":[" + String.join(",", crias) + "]}";
    }

    static String leche(String arete, LocalDate fecha, double litros) {
        return "{\"codigoArete\":\"" + arete + "\",\"fecha\":\"" + fecha + "\",\"litros\":" + litros + "}";
    }

    static String cuenta(String finca, String nombre, String correo, String contrasena) {
        return "{\"nombreFinca\":" + (finca == null ? "null" : "\"" + finca + "\"") + ",\"nombre\":\"" + nombre
                + "\",\"correo\":\"" + correo + "\",\"contrasena\":\"" + contrasena + "\"}";
    }

    static String ingreso(String correo, String contrasena) {
        return "{\"correo\":\"" + correo + "\",\"contrasena\":\"" + contrasena + "\"}";
    }

    private ClientePrueba fincaNueva(String nombreFinca, String correo) {
        ClientePrueba c = new ClientePrueba(puerto);
        esperar(201, c.post("/api/cuentas/fincas", cuenta(nombreFinca, "Prueba", correo, "Prueba2026")));
        return c;
    }

    private void vacaConParto(ClientePrueba c, String arete, int diasParto) {
        esperar(201, c.post("/api/animales", animal(arete, "HEMBRA", "2021-01-01", "LECHE")));
        esperar(201, c.post("/api/partos", parto(arete, HOY.minusDays(diasParto))));
    }

    private static String id(Respuesta r) {
        Matcher m = Pattern.compile("\"id\":(\\d+)").matcher(r.cuerpo());
        assertTrue(m.find(), r.cuerpo());
        return m.group(1);
    }

    // ---------- HU_001 Registrar animal ----------

    @Test
    @DisplayName("HU_001 C1 · Registrar animal con datos válidos")
    void hu001_c1_registrarConDatosValidos() {
        Respuesta r = administrador().post("/api/animales", animal("T-0101", "HEMBRA", HOY.toString(), "LECHE"));
        esperar(201, r, "\"codigoArete\":\"T-0101\"", "\"proposito\":\"LECHE\"");
        esperar(200, ganadero().get("/api/animales/T-0101"), "\"fechaNacimiento\":\"" + HOY + "\"");
    }

    @Test
    @DisplayName("HU_001 C2 · Propósitos según el sexo (la API acepta solo los que corresponden)")
    void hu001_c2_propositosSegunSexo() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-0121", "MACHO", null, "ENGORDE")));
        esperar(201, c.post("/api/animales", animal("T-0122", "MACHO", null, "PADRON")));
        esperar(201, c.post("/api/animales", animal("T-0123", "HEMBRA", null, "ENGORDE")));
        esperar(201, c.post("/api/animales", animal("T-0124", "HEMBRA", null, "LECHE")));
        esperar(422, c.post("/api/animales", animal("T-0125", "HEMBRA", null, "PADRON")), "Una hembra solo puede ser de engorde o leche.");
    }

    @Test
    @DisplayName("HU_001 C3 · Falta un dato obligatorio")
    void hu001_c3_faltaDatoObligatorio() {
        ClientePrueba c = administrador();
        esperar(400, c.post("/api/animales", "{\"sexo\":\"MACHO\",\"proposito\":\"ENGORDE\"}"), "\"codigoArete\":\"Escribe el código de arete.\"");
        esperar(400, c.post("/api/animales", "{\"codigoArete\":\"T-0131\",\"proposito\":\"ENGORDE\"}"), "\"sexo\":\"Elige el sexo.\"");
        esperar(400, c.post("/api/animales", "{\"codigoArete\":\"T-0131\",\"sexo\":\"MACHO\"}"), "\"proposito\":\"Elige el propósito.\"");
        esperar(404, c.get("/api/animales/T-0131"));
    }

    @Test
    @DisplayName("HU_001 C4 · Arete duplicado en la misma finca")
    void hu001_c4_areteDuplicado() {
        ClientePrueba c = administrador();
        esperar(422, c.post("/api/animales", animal("co-0142", "HEMBRA", null, "LECHE")), "El código de arete CO-0142 ya está registrado en la finca.");
        esperar(200, c.get("/api/animales/CO-0142"), "\"nombre\":\"Lucero\"");
    }

    @Test
    @DisplayName("HU_001 C5 · Mismo arete en otra finca")
    void hu001_c5_mismoAreteEnOtraFinca() {
        esperar(201, ClientePrueba.conSesion(puerto, OTRA_FINCA).post("/api/animales", animal("CO-0105", "MACHO", null, "PADRON")));
        esperar(200, ganadero().get("/api/animales/CO-0105"), "\"nombre\":\"Margarita\"");
    }

    @Test
    @DisplayName("HU_001 C6 · Fecha de nacimiento futura")
    void hu001_c6_fechaNacimientoFutura() {
        ClientePrueba c = administrador();
        esperar(422, c.post("/api/animales", animal("T-0161", "MACHO", HOY.plusDays(1).toString(), "ENGORDE")),
                "La fecha de nacimiento no puede ser posterior a hoy.");
        esperar(404, c.get("/api/animales/T-0161"));
    }

    @Test
    @DisplayName("HU_001 C7 · Propósito que no corresponde al sexo")
    void hu001_c7_propositoNoCorrespondeAlSexo() {
        ClientePrueba c = administrador();
        esperar(422, c.post("/api/animales", animal("T-0171", "MACHO", null, "LECHE")), "Un macho solo puede ser de engorde o padrón.");
        esperar(404, c.get("/api/animales/T-0171"));
    }

    // ---------- HU_002 Consultar y filtrar animales ----------

    @Test
    @DisplayName("HU_002 C1 · Consultar animales registrados")
    void hu002_c1_consultarListado() {
        esperar(200, ganadero().get("/api/animales"),
                "{\"codigoArete\":\"CO-0142\",\"nombre\":\"Lucero\",\"sexo\":\"HEMBRA\"", "\"proposito\":\"LECHE\"", "CO-0256", "CO-0201");
    }

    @Test
    @DisplayName("HU_002 C2 · Buscar por arete")
    void hu002_c2_buscarPorArete() {
        Respuesta r = ganadero().get("/api/animales?filtro=CO-0142");
        esperar(200, r, "Lucero");
        sinTexto(r, "CO-0105", "CO-0201");
    }

    @Test
    @DisplayName("HU_002 C3 · Filtrar el listado por sexo y por propósito")
    void hu002_c3_filtrar() {
        ClientePrueba c = ganadero();
        Respuesta hembras = c.get("/api/animales?sexo=HEMBRA");
        esperar(200, hembras, "CO-0142");
        sinTexto(hembras, "\"sexo\":\"MACHO\"");
        Respuesta machos = c.get("/api/animales?sexo=MACHO");
        esperar(200, machos, "CO-0201");
        sinTexto(machos, "\"sexo\":\"HEMBRA\"");
        Respuesta engorde = c.get("/api/animales?proposito=ENGORDE");
        esperar(200, engorde, "CO-0163");
        sinTexto(engorde, "\"proposito\":\"LECHE\"", "\"proposito\":\"PADRON\"");
        Respuesta lecheras = c.get("/api/animales?proposito=LECHE");
        esperar(200, lecheras, "CO-0105");
        sinTexto(lecheras, "\"proposito\":\"ENGORDE\"", "\"proposito\":\"PADRON\"");
        Respuesta padrones = c.get("/api/animales?proposito=PADRON");
        esperar(200, padrones, "CO-0201");
        sinTexto(padrones, "\"proposito\":\"ENGORDE\"", "\"proposito\":\"LECHE\"");
    }

    @Test
    @DisplayName("HU_002 C4 · Sin coincidencias (lista vacía; la pantalla muestra «sin registros»)")
    void hu002_c4_sinCoincidencias() {
        assertEquals("[]", ganadero().get("/api/animales?filtro=NO-EXISTE").cuerpo());
    }

    // ---------- HU_003 Consultar ficha del animal ----------

    @Test
    @DisplayName("HU_003 C1 · Ficha con fecha de nacimiento y edad en meses")
    void hu003_c1_fichaConEdad() {
        LocalDate nacimiento = HOY.minusMonths(14).minusDays(3);
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-0311", "HEMBRA", nacimiento.toString(), "ENGORDE")));
        esperar(200, c.get("/api/animales/T-0311"), "\"codigoArete\":\"T-0311\"", "\"nombre\":\"Prueba\"", "\"sexo\":\"HEMBRA\"",
                "\"fechaNacimiento\":\"" + nacimiento + "\"", "\"edadMeses\":14", "\"proposito\":\"ENGORDE\"");
    }

    @Test
    @DisplayName("HU_003 C2 · Animal sin fecha de nacimiento: no se calcula la edad")
    void hu003_c2_sinFechaNacimiento() {
        esperar(200, ganadero().get("/api/animales/CO-0256"), "\"fechaNacimiento\":null", "\"edadMeses\":null");
    }

    @Test
    @DisplayName("HU_003 C3 · Animal inexistente")
    void hu003_c3_animalInexistente() {
        esperar(404, ganadero().get("/api/animales/CO-9999"), "El animal CO-9999 no está registrado en la finca.");
    }

    // ---------- HU_004 Actualizar animal ----------

    @Test
    @DisplayName("HU_004 C1 · Actualizar nombre y fecha de nacimiento")
    void hu004_c1_actualizarDatos() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-0411", "MACHO", null, "ENGORDE")));
        String cambio = "{\"codigoArete\":\"T-0411\",\"nombre\":\"Relámpago\",\"sexo\":\"MACHO\",\"fechaNacimiento\":\"2025-01-01\",\"proposito\":\"ENGORDE\"}";
        esperar(200, c.put("/api/animales/T-0411", cambio), "\"nombre\":\"Relámpago\"", "\"fechaNacimiento\":\"2025-01-01\"");
        esperar(200, c.get("/api/animales/T-0411"), "\"nombre\":\"Relámpago\"");
    }

    @Test
    @DisplayName("HU_004 C2 · Cambiar el arete por uno disponible")
    void hu004_c2_cambiarAreteDisponible() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-0421", "MACHO", null, "ENGORDE")));
        esperar(200, c.put("/api/animales/T-0421", animal("T-0422", "MACHO", null, "ENGORDE")), "\"codigoArete\":\"T-0422\"");
        esperar(404, c.get("/api/animales/T-0421"));
        esperar(200, c.get("/api/animales/T-0422"));
    }

    @Test
    @DisplayName("HU_004 C3 · Cambiar el arete por uno ya registrado")
    void hu004_c3_cambiarAreteOcupado() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-0431", "MACHO", null, "ENGORDE")));
        esperar(422, c.put("/api/animales/T-0431", animal("CO-0142", "MACHO", null, "ENGORDE")),
                "El código de arete CO-0142 ya está registrado en la finca.", "Se conserva el arete T-0431");
        esperar(200, c.get("/api/animales/T-0431"));
    }

    @Test
    @DisplayName("HU_004 C4 · Cambiar el sexo obliga a elegir de nuevo el propósito")
    void hu004_c4_cambiarSexo() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-0441", "MACHO", null, "PADRON")));
        esperar(422, c.put("/api/animales/T-0441", animal("T-0441", "HEMBRA", null, "PADRON")),
                "Cambiaste el sexo. Elige de nuevo el propósito: engorde o leche.");
        esperar(200, c.put("/api/animales/T-0441", animal("T-0441", "HEMBRA", null, "LECHE")), "\"sexo\":\"HEMBRA\"", "\"proposito\":\"LECHE\"");
    }

    @Test
    @DisplayName("HU_004 C5 · Fecha de nacimiento futura al actualizar")
    void hu004_c5_fechaFutura() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-0451", "MACHO", "2024-05-05", "ENGORDE")));
        esperar(422, c.put("/api/animales/T-0451", animal("T-0451", "MACHO", HOY.plusDays(1).toString(), "ENGORDE")),
                "La fecha de nacimiento no puede ser posterior a hoy.");
        esperar(200, c.get("/api/animales/T-0451"), "\"fechaNacimiento\":\"2024-05-05\"");
    }

    // ---------- HU_005 Eliminar animal registrado por error ----------

    @Test
    @DisplayName("HU_005 C1 · Eliminar animal sin historial")
    void hu005_c1_eliminarSinHistorial() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-0511", "MACHO", null, "ENGORDE")));
        esperar(204, c.delete("/api/animales/T-0511"));
        esperar(404, c.get("/api/animales/T-0511"));
    }

    @Test
    @DisplayName("HU_005 C3 · Impedir eliminación con historial e informar qué tiene")
    void hu005_c3_impedirConHistorial() {
        ClientePrueba c = administrador();
        esperar(422, c.delete("/api/animales/CO-0142"), "No se puede eliminar Lucero", "registros sanitarios", "mediciones de peso",
                "partos y crías", "registros de leche");
        esperar(422, c.delete("/api/animales/CO-0201"), "registros sanitarios", "mediciones de peso");
        esperar(200, c.get("/api/animales/CO-0142"));
        // Una madre con cría registrada tampoco se puede eliminar, aunque no tenga otros registros.
        esperar(201, c.post("/api/animales", animal("T-0531", "HEMBRA", null, "ENGORDE")));
        esperar(201, c.post("/api/partos", parto("T-0531", HOY, animal("T-0532", "MACHO", null, "ENGORDE"))));
        esperar(422, c.delete("/api/animales/T-0531"), "partos y crías");
    }

    // ---------- HU_006 Registrar eventos sanitarios ----------

    @Test
    @DisplayName("HU_006 C1 · Registrar vacuna, enfermedad y tratamiento como realizados")
    void hu006_c1_registrarEvento() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-0611", "MACHO", null, "ENGORDE")));
        for (String tipo : new String[] {"VACUNA", "ENFERMEDAD", "TRATAMIENTO"}) {
            esperar(201, c.post("/api/sanitario/animales/T-0611/eventos", evento(tipo, "Evento " + tipo, HOY)),
                    "\"tipo\":\"" + tipo + "\"", "\"estado\":\"REALIZADA\"", "\"codigoArete\":\"T-0611\"", "\"observaciones\":\"Prueba\"");
        }
        Respuesta historial = c.get("/api/sanitario/animales/T-0611");
        esperar(200, historial, "Evento VACUNA", "Evento ENFERMEDAD", "Evento TRATAMIENTO");
    }

    @Test
    @DisplayName("HU_006 C2 · Animal no seleccionado o inexistente")
    void hu006_c2_animalInexistente() {
        esperar(422, administrador().post("/api/sanitario/animales/NO-EXISTE/eventos", evento("VACUNA", "Rabia", HOY)),
                "Debes seleccionar un animal registrado.");
    }

    // ---------- HU_007 Consultar historial sanitario ----------

    @Test
    @DisplayName("HU_007 C1 · Historial completo ordenado por fecha")
    void hu007_c1_historialOrdenado() {
        Respuesta r = ganadero().get("/api/sanitario/animales/CO-0142");
        esperar(200, r, "Aftosa", "Mastitis leve");
        // Del más reciente al más antiguo: Aftosa (02/10/2026) antes que Mastitis (15/08/2026).
        assertTrue(r.cuerpo().indexOf("2026-10-02") < r.cuerpo().indexOf("2026-08-15"), r.cuerpo());
    }

    @Test
    @DisplayName("HU_007 C2 · Filtrar por vacunas, enfermedades y tratamientos")
    void hu007_c2_filtrarPorTipo() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-0721", "HEMBRA", null, "ENGORDE")));
        for (String tipo : new String[] {"VACUNA", "ENFERMEDAD", "TRATAMIENTO"}) {
            esperar(201, c.post("/api/sanitario/animales/T-0721/eventos", evento(tipo, "Solo " + tipo, HOY)));
        }
        for (String tipo : new String[] {"VACUNA", "ENFERMEDAD", "TRATAMIENTO"}) {
            Respuesta r = c.get("/api/sanitario/animales/T-0721?tipo=" + tipo);
            esperar(200, r, "Solo " + tipo);
            for (String otro : new String[] {"VACUNA", "ENFERMEDAD", "TRATAMIENTO"}) {
                if (!otro.equals(tipo)) {
                    sinTexto(r, "Solo " + otro);
                }
            }
        }
    }

    @Test
    @DisplayName("HU_007 C3 · Las actividades programadas no aparecen en el historial")
    void hu007_c3_programadasNoAparecen() {
        Respuesta r = ganadero().get("/api/sanitario/animales/CO-0142");
        esperar(200, r, "Aftosa");
        sinTexto(r, "Brucelosis", "PROGRAMADA");
    }

    @Test
    @DisplayName("HU_007 C4 · Historial sin registros (lista vacía)")
    void hu007_c4_historialSinRegistros() {
        ClientePrueba c = ganadero();
        assertEquals("[]", c.get("/api/sanitario/animales/CO-0256").cuerpo());
        assertEquals("[]", c.get("/api/sanitario/animales/CO-0142?tipo=TRATAMIENTO").cuerpo());
    }

    // ---------- HU_008 Programar y completar actividad sanitaria ----------

    @Test
    @DisplayName("HU_008 C1 · Programar una actividad desde hoy")
    void hu008_c1_programar() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/sanitario/animales/CO-0163/programadas", programada("VACUNA", "Rabia hoy", HOY)), "\"estado\":\"PROGRAMADA\"",
                "\"fechaProgramada\":\"" + HOY + "\"", "\"fechaRealizacion\":null");
        esperar(201, c.post("/api/sanitario/animales/CO-0163/programadas", programada("VACUNA", "Rabia refuerzo", HOY.plusDays(30))),
                "\"estado\":\"PROGRAMADA\"");
    }

    @Test
    @DisplayName("HU_008 C2 · Rechazar fecha pasada")
    void hu008_c2_rechazarFechaPasada() {
        esperar(422, administrador().post("/api/sanitario/animales/CO-0163/programadas", programada("TRATAMIENTO", "Ayer", HOY.minusDays(1))),
                "La fecha programada no puede ser anterior a hoy.");
    }

    @Test
    @DisplayName("HU_008 C3 · Marcar como realizada sin perder la fecha programada")
    void hu008_c3_marcarRealizada() {
        ClientePrueba c = administrador();
        Respuesta prog = c.post("/api/sanitario/animales/CO-0212/programadas", programada("TRATAMIENTO", "Vitaminas AD3", HOY.plusDays(3)));
        esperar(201, prog);
        esperar(200, c.post("/api/sanitario/eventos/" + id(prog) + "/realizada?fechaRealizacion=" + HOY, "{}"),
                "\"estado\":\"REALIZADA\"", "\"fechaProgramada\":\"" + HOY.plusDays(3) + "\"", "\"fechaRealizacion\":\"" + HOY + "\"");
        esperar(422, c.post("/api/sanitario/eventos/" + id(prog) + "/realizada?fechaRealizacion=" + HOY, "{}"), "ya está marcada como realizada");
        esperar(200, c.get("/api/sanitario/animales/CO-0212"), "Vitaminas AD3");
    }

    // ---------- HU_009 Consultar actividades sanitarias ----------

    @Test
    @DisplayName("HU_009 C1 · Próximas actividades dentro del número de días")
    void hu009_c1_proximas() {
        ClientePrueba c = ganadero();
        esperar(201, c.post("/api/animales", animal("T-0911", "MACHO", null, "ENGORDE")));
        esperar(201, c.post("/api/sanitario/animales/T-0911/programadas", programada("VACUNA", "En cinco días", HOY.plusDays(5))));
        esperar(201, c.post("/api/sanitario/animales/T-0911/programadas", programada("VACUNA", "En cuarenta días", HOY.plusDays(40))));
        Respuesta r = c.get("/api/sanitario/proximas?dias=7");
        esperar(200, r, "En cinco días");
        sinTexto(r, "En cuarenta días");
        esperar(200, c.get("/api/sanitario/proximas?dias=45"), "En cuarenta días");
        esperar(422, c.get("/api/sanitario/proximas?dias=0"), "El número de días debe ser mayor que cero.");
    }

    @Test
    @DisplayName("HU_009 C2 · Pendientes vencidas")
    void hu009_c2_pendientesVencidas() {
        Respuesta r = ganadero().get("/api/sanitario/pendientes");
        esperar(200, r, "CO-0188", "CO-0240", "Vitaminas");
        sinTexto(r, "\"estado\":\"REALIZADA\"");
    }

    @Test
    @DisplayName("HU_009 C3 · Sin actividades (lista vacía)")
    void hu009_c3_sinActividades() {
        ClientePrueba c = fincaNueva("Sin Actividades", "sin.actividades@prueba.example");
        assertEquals("[]", c.get("/api/sanitario/pendientes").cuerpo());
        assertEquals("[]", c.get("/api/sanitario/proximas?dias=30").cuerpo());
    }

    // ---------- HU_010 Registrar y consultar peso ----------

    @Test
    @DisplayName("HU_010 C1 · Registrar una medición válida")
    void hu010_c1_registrarMedicion() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-1011", "MACHO", null, "ENGORDE")));
        esperar(201, c.post("/api/animales/T-1011/pesos", peso(250.5, HOY)), "\"pesoKg\":250.5", "\"fechaMedicion\":\"" + HOY + "\"");
        esperar(200, c.get("/api/animales/T-1011/pesos"), "250.5");
    }

    @Test
    @DisplayName("HU_010 C2 · Rechazar peso cero o negativo, sin animal o con fecha futura")
    void hu010_c2_rechazarMedicionInvalida() {
        ClientePrueba c = administrador();
        esperar(400, c.post("/api/animales/CO-0212/pesos", peso(0, HOY)), "\"pesoKg\":\"El peso debe ser mayor que cero.\"");
        esperar(400, c.post("/api/animales/CO-0212/pesos", peso(-5, HOY)), "El peso debe ser mayor que cero.");
        esperar(422, c.post("/api/animales/NO-EXISTE/pesos", peso(300, HOY)), "Debes seleccionar un animal registrado.");
        esperar(422, c.post("/api/animales/CO-0212/pesos", peso(300, HOY.plusDays(1))), "La fecha de medición no puede ser posterior a hoy.");
        sinTexto(c.get("/api/animales/CO-0212/pesos"), "\"pesoKg\":300.0");
    }

    @Test
    @DisplayName("HU_010 C3 · Historial ordenado con ganancia o pérdida")
    void hu010_c3_historialYVariaciones() {
        Respuesta r = ganadero().get("/api/animales/CO-0231/pesos");
        esperar(200, r, "\"diferenciaKg\":null", "\"diferenciaKg\":24.0", "\"diferenciaKg\":27.0", "\"diferenciaKg\":-3.0");
        String cuerpo = r.cuerpo();
        assertTrue(cuerpo.indexOf("2026-05-14") < cuerpo.indexOf("2026-08-14")
                && cuerpo.indexOf("2026-08-14") < cuerpo.indexOf("2026-10-14")
                && cuerpo.indexOf("2026-10-14") < cuerpo.indexOf("2026-11-14"), cuerpo);
    }

    @Test
    @DisplayName("HU_010 C4 · Sin mediciones (lista vacía)")
    void hu010_c4_sinMediciones() {
        assertEquals("[]", ganadero().get("/api/animales/CO-0256/pesos").cuerpo());
    }

    // ---------- HU_012 Consultar dashboard de la finca ----------

    @Test
    @DisplayName("HU_012 C1 · Resumen general del dashboard")
    void hu012_c1_resumenGeneral() {
        Respuesta r = ganadero().get("/api/reportes/dashboard");
        esperar(200, r, "resumen\\tanimales\\t", "resumen\\tvacunas\\t", "resumen\\tenfermedades\\t", "resumen\\ttratamientos\\t",
                "actividad\\tVENCIDA", "peso\\tCO-0142\\tLucero\\t432", "peso\\tCO-0231");
    }

    @Test
    @DisplayName("HU_012 C2 · Las actividades programadas no se cuentan como realizadas")
    void hu012_c2_programadasNoSeCuentan() {
        ClientePrueba c = fincaNueva("Conteo", "conteo@prueba.example");
        esperar(201, c.post("/api/animales", animal("C-1", "HEMBRA", null, "LECHE")));
        esperar(201, c.post("/api/sanitario/animales/C-1/eventos", evento("VACUNA", "Aftosa", HOY)));
        esperar(201, c.post("/api/sanitario/animales/C-1/programadas", programada("VACUNA", "Brucelosis", HOY.plusDays(3))));
        esperar(200, c.get("/api/reportes/dashboard"), "resumen\\tvacunas\\t1\"", "actividad\\tPROXIMA\\t" + HOY.plusDays(3) + "\\tC-1");
    }

    @Test
    @DisplayName("HU_012 C3 · Sección sin datos: no hay filas inventadas")
    void hu012_c3_seccionSinDatos() {
        Respuesta r = fincaNueva("Vacía", "vacia@prueba.example").get("/api/reportes/dashboard");
        esperar(200, r, "resumen\\tanimales\\t0\"", "resumen\\tvacunas\\t0\"");
        sinTexto(r, "actividad\\t", "peso\\t");
    }

    // ---------- HU_014 Generar reportes ----------

    @Test
    @DisplayName("HU_014 C1 · Reportes de animales, sanitario y peso")
    void hu014_c1_generarReporte() {
        ClientePrueba c = administrador();
        esperar(200, c.post("/api/reportes", "{\"tipo\":\"ANIMALES\"}"), "\"tipo\":\"ANIMALES\"", "CO-0142\\tLucero\\tHEMBRA\\tLECHE");
        esperar(200, c.post("/api/reportes", "{\"tipo\":\"SANITARIO\",\"codigoArete\":\"CO-0142\"}"), "2026-10-02\\tVACUNA\\tAftosa\\tCiclo II");
        esperar(200, c.post("/api/reportes", "{\"tipo\":\"PESO\",\"codigoArete\":\"CO-0231\"}"), "2026-11-14\\t286\\t-3");
    }

    @Test
    @DisplayName("HU_014 C2 · Reporte sin datos: sin filas inventadas")
    void hu014_c2_reporteSinDatos() {
        ClientePrueba c = ganadero();
        esperar(200, c.post("/api/reportes", "{\"tipo\":\"SANITARIO\",\"codigoArete\":\"CO-0256\"}"), "\"filas\":[]");
        esperar(200, c.post("/api/reportes", "{\"tipo\":\"PESO\",\"codigoArete\":\"CO-0256\"}"), "\"filas\":[]");
        esperar(200, fincaNueva("Sin Animales", "sin.animales@prueba.example").post("/api/reportes", "{\"tipo\":\"ANIMALES\"}"), "\"filas\":[]");
    }

    // ---------- HU_015 Pasar un animal a engorde ----------

    @Test
    @DisplayName("HU_015 C1 · Pasar a engorde una vaca de leche y un macho padrón conservando su historial")
    void hu015_c1_pasarAEngorde() {
        ClientePrueba c = ganadero();
        vacaConParto(c, "T-1511", 30);
        esperar(201, c.post("/api/leche", leche("T-1511", HOY.minusDays(1), 15)));
        esperar(201, c.post("/api/animales", animal("T-1512", "MACHO", "2022-01-01", "PADRON")));
        esperar(201, c.post("/api/animales/T-1512/pesos", peso(480, HOY)));
        esperar(200, c.post("/api/animales/T-1511/engorde", "{}"), "\"proposito\":\"ENGORDE\"");
        esperar(200, c.post("/api/animales/T-1512/engorde", "{}"), "\"proposito\":\"ENGORDE\"");
        esperar(200, c.get("/api/partos/madres/T-1511"), "\"fecha\":\"" + HOY.minusDays(30) + "\"");
        esperar(200, c.get("/api/leche/T-1511/promedios?periodo=PARTO"), "\"diasConRegistro\":1");
        esperar(200, c.get("/api/animales/T-1512/pesos"), "480");
    }

    @Test
    @DisplayName("HU_015 C2 · Ya no se registra leche después del cambio")
    void hu015_c2_sinLecheDespuesDelCambio() {
        ClientePrueba c = ganadero();
        vacaConParto(c, "T-1521", 10);
        esperar(200, c.post("/api/animales/T-1521/engorde", "{}"));
        esperar(422, c.post("/api/leche", leche("T-1521", HOY, 10)), "su propósito es engorde, no leche");
    }

    @Test
    @DisplayName("HU_015 C3 · Cambio no permitido si ya es de engorde")
    void hu015_c3_cambioNoPermitido() {
        esperar(422, ganadero().post("/api/animales/CO-0163/engorde", "{}"), "ya es de engorde");
    }

    // ---------- HU_016 Registrar parto y crías ----------

    @Test
    @DisplayName("HU_016 C1 · Parto con su cría: fecha de nacimiento = parto y asociada a la madre")
    void hu016_c1_partoConCria() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-1611", "HEMBRA", "2021-01-01", "LECHE")));
        LocalDate fecha = HOY.minusDays(2);
        String cria = "{\"codigoArete\":\"T-1612\",\"nombre\":\"Brisa\",\"sexo\":\"HEMBRA\",\"proposito\":\"LECHE\"}";
        esperar(201, c.post("/api/partos", parto("T-1611", fecha, cria)), "T-1612");
        esperar(200, c.get("/api/animales/T-1612"), "\"nombre\":\"Brisa\"", "\"fechaNacimiento\":\"" + fecha + "\"");
        esperar(200, c.get("/api/partos/madres/T-1611"), "\"fecha\":\"" + fecha + "\"", "T-1612");
        assertEquals("T-1611", bd().queryForObject("""
                select m.codigo_arete from animales c join partos p on p.id = c.parto_id join animales m on m.id = p.madre_id
                where c.codigo_arete = 'T-1612'""", String.class));
    }

    @Test
    @DisplayName("HU_016 C2 · Propósito de la cría según el sexo")
    void hu016_c2_propositoCria() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-1621", "HEMBRA", null, "LECHE")));
        esperar(422, c.post("/api/partos", parto("T-1621", HOY, animal("T-1622", "MACHO", null, "LECHE"))), "Un macho solo puede ser de engorde o padrón.");
        esperar(422, c.post("/api/partos", parto("T-1621", HOY, animal("T-1622", "HEMBRA", null, "PADRON"))), "Una hembra solo puede ser de engorde o leche.");
        assertEquals("[]", c.get("/api/partos/madres/T-1621").cuerpo());
    }

    @Test
    @DisplayName("HU_016 C3 · Parto con dos crías")
    void hu016_c3_dosCrias() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-1631", "HEMBRA", null, "LECHE")));
        esperar(201, c.post("/api/partos", parto("T-1631", HOY, animal("T-1632", "HEMBRA", null, "LECHE"), animal("T-1633", "MACHO", null, "PADRON"))));
        assertEquals(1, bd().queryForObject("""
                select count(distinct c.parto_id) from animales c where c.codigo_arete in ('T-1632', 'T-1633')""", Integer.class));
        esperar(200, c.get("/api/partos/madres/T-1631"), "T-1632", "T-1633");
    }

    @Test
    @DisplayName("HU_016 C4 · Parto sin crías")
    void hu016_c4_partoSinCrias() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-1641", "HEMBRA", null, "LECHE")));
        esperar(201, c.post("/api/partos", parto("T-1641", HOY.minusDays(1))), "\"crias\":[]");
        esperar(200, c.get("/api/partos/madres/T-1641"), "\"fecha\":\"" + HOY.minusDays(1) + "\",\"crias\":[]");
    }

    @Test
    @DisplayName("HU_016 C5 · Madre inválida: macho o inexistente")
    void hu016_c5_madreInvalida() {
        ClientePrueba c = administrador();
        esperar(422, c.post("/api/partos", parto("CO-0201", HOY)), "Selecciona una hembra registrada.");
        esperar(422, c.post("/api/partos", parto("NO-EXISTE", HOY)), "Selecciona una hembra registrada.");
        assertEquals("[]", c.get("/api/partos/madres/CO-0201").cuerpo());
    }

    @Test
    @DisplayName("HU_016 C6 · Fecha de parto futura")
    void hu016_c6_fechaFutura() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-1661", "HEMBRA", null, "LECHE")));
        esperar(422, c.post("/api/partos", parto("T-1661", HOY.plusDays(1))), "La fecha del parto no puede ser posterior a hoy.");
        assertEquals("[]", c.get("/api/partos/madres/T-1661").cuerpo());
    }

    @Test
    @DisplayName("HU_016 C7 · Arete de la cría duplicado: no se guarda nada")
    void hu016_c7_areteCriaDuplicado() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-1671", "HEMBRA", null, "LECHE")));
        esperar(422, c.post("/api/partos", parto("T-1671", HOY, animal("CO-0142", "HEMBRA", null, "LECHE"))),
                "El código de arete CO-0142 ya está registrado en la finca.");
        assertEquals("[]", c.get("/api/partos/madres/T-1671").cuerpo());
    }

    // ---------- HU_017 Consultar historial reproductivo ----------

    @Test
    @DisplayName("HU_017 C1 · Partos ordenados por fecha con las crías de cada uno")
    void hu017_c1_partosDeUnaVaca() {
        Respuesta r = ganadero().get("/api/partos/madres/CO-0142");
        esperar(200, r, "\"codigoArete\":\"CO-0188\",\"nombre\":\"Canela\",\"sexo\":\"HEMBRA\"", "\"proposito\":\"LECHE\"",
                "\"codigoArete\":\"CO-0231\",\"nombre\":\"Tizón\",\"sexo\":\"MACHO\"", "\"proposito\":\"ENGORDE\"");
        assertTrue(r.cuerpo().indexOf("\"fecha\":\"2023-05-10\"") < r.cuerpo().indexOf("\"fecha\":\"2025-02-18\""), r.cuerpo());
    }

    @Test
    @DisplayName("HU_017 C2 · Vaca sin partos (lista vacía)")
    void hu017_c2_vacaSinPartos() {
        assertEquals("[]", ganadero().get("/api/partos/madres/CO-0249").cuerpo());
    }

    // ---------- HU_018 Registrar producción diaria de leche ----------

    @Test
    @DisplayName("HU_018 C1 · Registrar la leche de un día en el parto más reciente")
    void hu018_c1_registrarLeche() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-1811", "HEMBRA", "2020-01-01", "LECHE")));
        esperar(201, c.post("/api/partos", parto("T-1811", HOY.minusDays(400))));
        esperar(201, c.post("/api/partos", parto("T-1811", HOY.minusDays(20))));
        esperar(201, c.post("/api/leche", leche("T-1811", HOY, 17.5)), "\"litros\":17.5", "\"fecha\":\"" + HOY + "\"");
        assertEquals(HOY.minusDays(20).toString(), bd().queryForObject("""
                select p.fecha::text from registros_leche r join partos p on p.id = r.parto_id join animales a on a.id = r.animal_id
                where a.codigo_arete = 'T-1811'""", String.class));
    }

    @Test
    @DisplayName("HU_018 C2 · Rechazar litros, fechas o días repetidos")
    void hu018_c2_rechazarRegistroInvalido() {
        ClientePrueba c = administrador();
        vacaConParto(c, "T-1821", 15);
        esperar(400, c.post("/api/leche", leche("T-1821", HOY, 0)), "\"litros\":\"Los litros deben ser mayores que cero.\"");
        esperar(400, c.post("/api/leche", leche("T-1821", HOY, -2)), "Los litros deben ser mayores que cero.");
        esperar(422, c.post("/api/leche", leche("T-1821", HOY.plusDays(1), 10)), "La fecha no puede ser posterior a hoy.");
        esperar(422, c.post("/api/leche", leche("T-1821", HOY.minusDays(16), 10)), "La fecha no puede ser anterior al parto");
        esperar(201, c.post("/api/leche", leche("T-1821", HOY, 10)));
        esperar(422, c.post("/api/leche", leche("T-1821", HOY, 12)), "ya está registrada para");
        esperar(200, c.get("/api/leche/T-1821/promedios?periodo=PARTO"), "\"diasConRegistro\":1", "\"totalLitros\":10.0");
    }

    @Test
    @DisplayName("HU_018 C3 · Hembra que no es de leche o sin partos")
    void hu018_c3_hembraSinLecheOSinPartos() {
        ClientePrueba c = administrador();
        esperar(422, c.post("/api/leche", leche("CO-0240", HOY, 10)), "No se puede registrar leche de", "su propósito es engorde, no leche");
        esperar(201, c.post("/api/animales", animal("T-1831", "HEMBRA", "2021-01-01", "LECHE")));
        esperar(422, c.post("/api/leche", leche("T-1831", HOY, 10)), "no tiene partos registrados");
    }

    // ---------- HU_019 Consultar promedios de leche ----------

    private LocalDate lunesPasado() {
        return HOY.minusWeeks(1).with(DayOfWeek.MONDAY);
    }

    @Test
    @DisplayName("HU_019 C1 · Semana completa: 20, 22, 21, 19, 20, 23 y 22 litros → 21 por día")
    void hu019_c1_promedioSemana() {
        ClientePrueba c = ganadero();
        vacaConParto(c, "T-1911", 30);
        double[] litros = {20, 22, 21, 19, 20, 23, 22};
        LocalDate lunes = lunesPasado();
        for (int i = 0; i < litros.length; i++) {
            esperar(201, c.post("/api/leche", leche("T-1911", lunes.plusDays(i), litros[i])));
        }
        Respuesta r = c.get("/api/leche/T-1911/promedios?periodo=SEMANA&fecha=" + lunes.plusDays(3));
        esperar(200, r, "\"diasConRegistro\":7", "\"totalLitros\":147.0", "\"promedioDiario\":21.0", "\"fecha\":\"" + lunes + "\"",
                "\"fecha\":\"" + lunes.plusDays(6) + "\"");
    }

    @Test
    @DisplayName("HU_019 C2 · Semana con días sin registro: 18, 20 y 22 → 20 por día")
    void hu019_c2_semanaConDiasSinRegistro() {
        ClientePrueba c = ganadero();
        vacaConParto(c, "T-1921", 30);
        LocalDate lunes = lunesPasado();
        double[] litros = {18, 20, 22};
        for (int i = 0; i < litros.length; i++) {
            esperar(201, c.post("/api/leche", leche("T-1921", lunes.plusDays(i * 2), litros[i])));
        }
        esperar(200, c.get("/api/leche/T-1921/promedios?periodo=SEMANA&fecha=" + lunes), "\"diasConRegistro\":3", "\"promedioDiario\":20.0");
    }

    @Test
    @DisplayName("HU_019 C3 · Promedio por semana, mes y parto")
    void hu019_c3_promediosPorPeriodo() {
        ClientePrueba c = ganadero();
        LocalDate primeroMesPasado = HOY.minusMonths(1).withDayOfMonth(1);
        esperar(201, c.post("/api/animales", animal("T-1931", "HEMBRA", "2020-01-01", "LECHE")));
        esperar(201, c.post("/api/partos", parto("T-1931", primeroMesPasado)));
        // Mes pasado: 10 y 20 litros. Mes actual: 30 litros (el 1 del mes).
        esperar(201, c.post("/api/leche", leche("T-1931", primeroMesPasado, 10)));
        esperar(201, c.post("/api/leche", leche("T-1931", primeroMesPasado.plusDays(1), 20)));
        esperar(201, c.post("/api/leche", leche("T-1931", HOY.withDayOfMonth(1), 30)));
        esperar(200, c.get("/api/leche/T-1931/promedios?periodo=MES&fecha=" + primeroMesPasado), "\"periodo\":\"MES\"",
                "\"diasConRegistro\":2", "\"totalLitros\":30.0", "\"promedioDiario\":15.0");
        esperar(200, c.get("/api/leche/T-1931/promedios?periodo=PARTO"), "\"periodo\":\"PARTO\"",
                "\"diasConRegistro\":3", "\"totalLitros\":60.0", "\"promedioDiario\":20.0");
        esperar(200, c.get("/api/leche/T-1931/promedios?periodo=SEMANA&fecha=" + primeroMesPasado), "\"periodo\":\"SEMANA\"");
        esperar(422, c.get("/api/leche/T-1931/promedios?periodo=ANIO"), "El periodo debe ser SEMANA, MES o PARTO.");
    }

    @Test
    @DisplayName("HU_019 C4 · Periodo sin registros: no se calcula promedio")
    void hu019_c4_periodoSinRegistros() {
        esperar(200, ganadero().get("/api/leche/CO-0142/promedios?periodo=MES&fecha=2020-01-15"), "\"diasConRegistro\":0", "\"registros\":[]");
    }

    // ---------- HU_020 Registrar finca ----------

    @Test
    @DisplayName("HU_020 C1 · Registrar una finca nueva y entrar")
    void hu020_c1_registrarFinca() {
        ClientePrueba c = new ClientePrueba(puerto);
        esperar(201, c.post("/api/cuentas/fincas", cuenta("El Paraíso", "Rosa", "rosa@elparaiso.example", "Paraiso2026")),
                "\"rol\":\"GANADERO\"", "\"nombreFinca\":\"El Paraíso\"");
        esperar(200, c.get("/api/sesion"), "Rosa");
        esperar(200, new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("rosa@elparaiso.example", "Paraiso2026")), "El Paraíso");
    }

    @Test
    @DisplayName("HU_020 C2 · Correo ya registrado")
    void hu020_c2_correoYaRegistrado() {
        esperar(422, new ClientePrueba(puerto).post("/api/cuentas/fincas", cuenta("Repetida", "Otro", ADMIN, "Repetida2026")),
                "El correo ya está registrado.");
        assertEquals(0, bd().queryForObject("select count(*) from fincas where nombre = 'Repetida'", Integer.class));
    }

    @Test
    @DisplayName("HU_020 C3 · Datos incompletos o inválidos")
    void hu020_c3_datosInvalidos() {
        ClientePrueba c = new ClientePrueba(puerto);
        esperar(422, c.post("/api/cuentas/fincas", cuenta(null, "Ana", "ana@x.example", "Clave123456")), "Escribe el nombre de la finca.");
        esperar(400, c.post("/api/cuentas/fincas", cuenta("Finca Ana", "", "ana@x.example", "Clave123456")), "\"nombre\":\"Escribe el nombre.\"");
        esperar(400, c.post("/api/cuentas/fincas", cuenta("Finca Ana", "Ana", "", "Clave123456")), "\"correo\":\"Escribe el correo.\"");
        esperar(400, c.post("/api/cuentas/fincas", cuenta("Finca Ana", "Ana", "ana@x.example", "Clave1234")),
                "La contraseña debe tener entre 10 y 12 caracteres.");
        assertEquals(0, bd().queryForObject("select count(*) from usuarios where correo = 'ana@x.example'", Integer.class));
    }

    // ---------- HU_021 Iniciar y cerrar sesión ----------

    @Test
    @DisplayName("HU_021 C1 · Iniciar sesión con datos correctos")
    void hu021_c1_ingresoCorrecto() {
        ClientePrueba g = new ClientePrueba(puerto);
        esperar(200, g.post("/api/sesion/ingreso", ingreso(GANADERO, "Esperanza26")), "\"rol\":\"GANADERO\"", "La Esperanza");
        esperar(200, g.get("/api/reportes/dashboard"));
        ClientePrueba a = new ClientePrueba(puerto);
        esperar(200, a.post("/api/sesion/ingreso", ingreso(ADMIN, "Esperanza26")), "\"rol\":\"ADMINISTRADOR\"");
        esperar(200, a.get("/api/animales"), "CO-0142");
    }

    @Test
    @DisplayName("HU_021 C2 · Datos incorrectos sin decir cuál falló")
    void hu021_c2_datosIncorrectos() {
        ClientePrueba c = new ClientePrueba(puerto);
        Respuesta claveMala = c.post("/api/sesion/ingreso", ingreso(GANADERO, "otraClave12"));
        Respuesta correoMalo = c.post("/api/sesion/ingreso", ingreso("nadie@x.example", "Esperanza26"));
        esperar(403, claveMala, "El correo o la contraseña no son correctos.");
        assertEquals(claveMala.cuerpo(), correoMalo.cuerpo());
        esperar(401, c.get("/api/animales"));
    }

    @Test
    @DisplayName("HU_021 C3 · Cuenta desactivada")
    void hu021_c3_cuentaDesactivada() {
        ClientePrueba g = ganadero();
        esperar(201, g.post("/api/cuentas/administradores", cuenta(null, "Luis", "luis@laesperanza.example", "Inicial2026")));
        esperar(204, g.post("/api/cuentas/administradores/desactivacion", "{\"correo\":\"luis@laesperanza.example\"}"));
        ClientePrueba luis = new ClientePrueba(puerto);
        esperar(403, luis.post("/api/sesion/ingreso", ingreso("luis@laesperanza.example", "Inicial2026")),
                "Tu cuenta está desactivada. Habla con el ganadero de tu finca.");
        esperar(401, luis.get("/api/animales"));
    }

    @Test
    @DisplayName("HU_021 C4 · Cerrar sesión: no hay información hasta entrar de nuevo")
    void hu021_c4_cerrarSesion() {
        ClientePrueba c = ganadero();
        esperar(200, c.get("/api/sesion"), "\"rol\":\"GANADERO\"");
        esperar(204, c.post("/api/sesion/salida", "{}"));
        esperar(401, c.get("/api/sesion"));
        esperar(401, c.get("/api/animales"), "Inicia sesión para continuar.");
        esperar(401, c.get("/api/reportes/dashboard"));
    }

    // ---------- HU_022 Permisos por usuario y separación de fincas ----------

    @Test
    @DisplayName("HU_022 C2 · El administrador registra, corrige y elimina animales y genera reportes")
    void hu022_c2_funcionesDelAdministrador() {
        ClientePrueba c = administrador();
        esperar(201, c.post("/api/animales", animal("T-2221", "HEMBRA", null, "ENGORDE")));
        esperar(200, c.put("/api/animales/T-2221", animal("T-2221", "HEMBRA", "2025-02-02", "ENGORDE")));
        esperar(204, c.delete("/api/animales/T-2221"));
        esperar(200, c.post("/api/reportes", "{\"tipo\":\"ANIMALES\"}"));
    }

    @Test
    @DisplayName("HU_022 C3 · Intento directo de una función del ganadero: se rechaza y no cambia nada")
    void hu022_c3_intentoDirectoRechazado() {
        ClientePrueba c = administrador();
        esperar(403, c.get("/api/reportes/dashboard"), "Esta opción es solo del ganadero. No se cambió ningún dato.");
        esperar(403, c.get("/api/cuentas/administradores"));
        esperar(403, c.post("/api/cuentas/administradores", cuenta(null, "Intruso", "intruso@x.example", "Clave123456")));
        esperar(403, c.post("/api/cuentas/administradores/desactivacion", "{\"correo\":\"andres@laesperanza.example\"}"));
        esperar(403, c.post("/api/cuentas/administradores/contrasena", ingreso("andres@laesperanza.example", "Cambiada2026")));
        assertEquals(0, bd().queryForObject("select count(*) from usuarios where correo = 'intruso@x.example'", Integer.class));
        assertTrue(bd().queryForObject("select activo from usuarios where correo = 'andres@laesperanza.example'", Boolean.class));
        esperar(200, new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("andres@laesperanza.example", "Esperanza26")));
    }

    @Test
    @DisplayName("HU_022 C4 · Datos de otra finca: solo se ven los propios")
    void hu022_c4_datosDeOtraFinca() {
        ClientePrueba marta = ClientePrueba.conSesion(puerto, OTRA_FINCA);
        Respuesta lista = marta.get("/api/animales");
        esperar(200, lista, "RS-0001");
        sinTexto(lista, "CO-0142", "Lucero");
        esperar(404, marta.get("/api/animales/CO-0142"), "no está registrado");
        esperar(404, marta.get("/api/sanitario/animales/CO-0142"));
        esperar(404, marta.get("/api/animales/CO-0231/pesos"));
        esperar(404, marta.post("/api/reportes", "{\"tipo\":\"PESO\",\"codigoArete\":\"CO-0231\"}"));
        sinTexto(marta.post("/api/reportes", "{\"tipo\":\"ANIMALES\"}"), "CO-0142");
        sinTexto(marta.get("/api/sanitario/pendientes"), "CO-0188", "CO-0240");
    }

    // ---------- HU_023 Gestionar administradores ----------

    @Test
    @DisplayName("HU_023 C1 · Crear un administrador que puede entrar")
    void hu023_c1_crearAdministrador() {
        esperar(201, ganadero().post("/api/cuentas/administradores", cuenta(null, "Sofía", "sofia@laesperanza.example", "Inicial2026")),
                "\"rol\":\"ADMINISTRADOR\"", "\"activo\":true");
        esperar(200, new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("sofia@laesperanza.example", "Inicial2026")), "La Esperanza");
    }

    @Test
    @DisplayName("HU_023 C2 · Correo ya registrado")
    void hu023_c2_correoYaRegistrado() {
        esperar(422, ganadero().post("/api/cuentas/administradores", cuenta(null, "Otra", OTRA_FINCA, "Inicial2026")), "El correo ya está registrado.");
    }

    @Test
    @DisplayName("HU_023 C3 · Lista de administradores con nombre, correo y estado")
    void hu023_c3_listaAdministradores() {
        Respuesta r = ganadero().get("/api/cuentas/administradores");
        esperar(200, r, "\"nombre\":\"Camila Rosero\",\"correo\":\"camila@laesperanza.example\"", "\"activo\":true", "Andrés Muñoz");
        sinTexto(r, "jaider@", "marta@", "hash", "contrasena");
    }

    @Test
    @DisplayName("HU_023 C4 · Desactivar: ya no entra y sus registros se conservan")
    void hu023_c4_desactivar() {
        ClientePrueba g = ganadero();
        esperar(201, g.post("/api/cuentas/administradores", cuenta(null, "Pedro", "pedro@laesperanza.example", "Inicial2026")));
        ClientePrueba pedro = new ClientePrueba(puerto);
        esperar(200, pedro.post("/api/sesion/ingreso", ingreso("pedro@laesperanza.example", "Inicial2026")));
        esperar(201, pedro.post("/api/animales", animal("T-2341", "MACHO", null, "ENGORDE")));
        esperar(204, g.post("/api/cuentas/administradores/desactivacion", "{\"correo\":\"pedro@laesperanza.example\"}"));
        esperar(403, new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("pedro@laesperanza.example", "Inicial2026")), "desactivada");
        esperar(200, g.get("/api/animales/T-2341"));
        esperar(200, g.get("/api/cuentas/administradores"), "\"nombre\":\"Pedro\",\"correo\":\"pedro@laesperanza.example\",\"rol\":\"ADMINISTRADOR\",\"activo\":false");
    }

    @Test
    @DisplayName("HU_023 C5 · Restablecer contraseña: entra con la nueva, la anterior deja de servir")
    void hu023_c5_restablecerContrasena() {
        ClientePrueba g = ganadero();
        esperar(201, g.post("/api/cuentas/administradores", cuenta(null, "Nora", "nora@laesperanza.example", "Inicial2026")));
        esperar(422, g.post("/api/cuentas/administradores/contrasena", ingreso("nora@laesperanza.example", "Corta")), "entre 10 y 12");
        esperar(204, g.post("/api/cuentas/administradores/contrasena", ingreso("nora@laesperanza.example", "Nueva123456")));
        esperar(200, new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("nora@laesperanza.example", "Nueva123456")));
        esperar(403, new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("nora@laesperanza.example", "Inicial2026")),
                "El correo o la contraseña no son correctos.");
        esperar(404, g.post("/api/cuentas/administradores/contrasena", ingreso(OTRA_FINCA, "Nueva123456")), "No hay un administrador con ese correo en la finca.");
    }

    // ---------- HU_024 Recuperar contraseña del ganadero ----------

    @Test
    @DisplayName("HU_024 C1 · Solicitar la recuperación envía un enlace al correo del ganadero")
    void hu024_c1_solicitarRecuperacion() {
        fincaNueva("Recupera Uno", "recupera1@prueba.example");
        esperar(200, new ClientePrueba(puerto).post("/api/sesion/recuperacion", "{\"correo\":\"recupera1@prueba.example\"}"));
        String enlace = SeguridadTest.CorreoDePrueba.ultimoEnlace("recupera1@prueba.example");
        assertTrue(enlace.startsWith("http://localhost:5173/recuperar/"), enlace);
    }

    @Test
    @DisplayName("HU_024 C2 · Crear la contraseña nueva con el enlace; el enlace deja de funcionar")
    void hu024_c2_crearContrasenaNueva() {
        fincaNueva("Recupera Dos", "recupera2@prueba.example");
        ClientePrueba c = new ClientePrueba(puerto);
        esperar(200, c.post("/api/sesion/recuperacion", "{\"correo\":\"recupera2@prueba.example\"}"));
        String token = SeguridadTest.CorreoDePrueba.token("recupera2@prueba.example");
        esperar(422, c.post("/api/sesion/recuperacion/" + token, "{\"contrasena\":\"Corta\"}"), "entre 10 y 12");
        esperar(204, c.post("/api/sesion/recuperacion/" + token, "{\"contrasena\":\"Recuperada26\"}"));
        esperar(200, new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("recupera2@prueba.example", "Recuperada26")));
        esperar(403, new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("recupera2@prueba.example", "Prueba2026")));
        esperar(422, c.post("/api/sesion/recuperacion/" + token, "{\"contrasena\":\"OtraVez2026\"}"), "Solicita un enlace nuevo.");
    }

    @Test
    @DisplayName("HU_024 C3 · Enlace vencido (más de 30 minutos) o ya usado")
    void hu024_c3_enlaceVencidoOUsado() {
        fincaNueva("Recupera Tres", "recupera3@prueba.example");
        ClientePrueba c = new ClientePrueba(puerto);
        esperar(200, c.post("/api/sesion/recuperacion", "{\"correo\":\"recupera3@prueba.example\"}"));
        String token = SeguridadTest.CorreoDePrueba.token("recupera3@prueba.example");
        // Simula que pasaron 31 minutos.
        bd().update("""
                update tokens_recuperacion set vence_en = vence_en - interval '31 minutes'
                where usuario_id = (select id from usuarios where correo = 'recupera3@prueba.example')""");
        esperar(422, c.post("/api/sesion/recuperacion/" + token, "{\"contrasena\":\"Vencida2026\"}"),
                "El enlace venció o ya se usó. Solicita un enlace nuevo.");
        esperar(422, c.post("/api/sesion/recuperacion/token-falso", "{\"contrasena\":\"Vencida2026\"}"), "Solicita un enlace nuevo.");
        esperar(200, new ClientePrueba(puerto).post("/api/sesion/ingreso", ingreso("recupera3@prueba.example", "Prueba2026")));
    }

    @Test
    @DisplayName("HU_024 C4 · Correo que no es de un ganadero: mismo mensaje y no se envía enlace")
    void hu024_c4_correoNoGanadero() {
        ClientePrueba c = new ClientePrueba(puerto);
        fincaNueva("Recupera Cuatro", "recupera4@prueba.example");
        Respuesta valida = c.post("/api/sesion/recuperacion", "{\"correo\":\"recupera4@prueba.example\"}");
        Respuesta admin = c.post("/api/sesion/recuperacion", "{\"correo\":\"" + ADMIN + "\"}");
        Respuesta noExiste = c.post("/api/sesion/recuperacion", "{\"correo\":\"nadie@x.example\"}");
        esperar(200, valida);
        assertEquals(valida.cuerpo(), admin.cuerpo());
        assertEquals(valida.cuerpo(), noExiste.cuerpo());
        assertFalse(SeguridadTest.CorreoDePrueba.recibio(ADMIN));
        assertFalse(SeguridadTest.CorreoDePrueba.recibio("nadie@x.example"));
    }
}
