package com.hatocontrol.presentacion;

import static com.hatocontrol.presentacion.CriteriosAceptacionTest.animal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hatocontrol.presentacion.ClientePrueba.Respuesta;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
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
class CapacidadTest {

    static final int FINCAS = 100;
    static final int ANIMALES_POR_FINCA = 40;
    static final long LIMITE_MS = 3000;
    static final int REPETICIONES = 5;
    static final LocalDate HOY = LocalDate.now(ZoneId.of("America/Bogota"));

    @LocalServerPort
    int puerto;

    @Autowired
    DataSource fuenteDatos;

    private void crearFincas(JdbcTemplate bd) {
        String hash = bd.queryForObject("select hash_contrasena from usuarios where correo = ?", String.class,
                CriteriosAceptacionTest.GANADERO);
        bd.update("""
                insert into fincas (nombre, fecha_registro)
                select 'Capacidad ' || lpad(n::text, 3, '0'), current_date - 300 from generate_series(1, ?) n""", FINCAS);
        bd.update("""
                insert into usuarios (finca_id, nombre, correo, hash_contrasena, rol, activo)
                select f.id, 'Ganadero ' || f.nombre, 'capacidad' || right(f.nombre, 3) || '@prueba.example', ?, 'GANADERO', true
                from fincas f where f.nombre like 'Capacidad %'""", hash);
        bd.update("""
                insert into animales (finca_id, codigo_arete, nombre, sexo, fecha_nacimiento, proposito)
                select f.id, 'CAP-' || lpad(n::text, 4, '0'), 'Animal ' || n,
                       case when n % 2 = 0 then 'HEMBRA' else 'MACHO' end,
                       current_date - (700 + n * 10),
                       case when n % 2 = 0 then 'LECHE' else 'ENGORDE' end
                from fincas f cross join generate_series(1, ?) n where f.nombre like 'Capacidad %'""", ANIMALES_POR_FINCA);
        bd.update("""
                insert into partos (madre_id, fecha)
                select a.id, current_date - 200 from animales a join fincas f on f.id = a.finca_id
                where f.nombre like 'Capacidad %' and a.sexo = 'HEMBRA'""");
        bd.update("""
                insert into registros_leche (animal_id, parto_id, fecha, litros)
                select p.madre_id, p.id, d::date, 15 + (extract(doy from d)::int % 7)
                from partos p join animales a on a.id = p.madre_id join fincas f on f.id = a.finca_id
                cross join lateral generate_series(p.fecha, current_date - 1, interval '1 day') d
                where f.nombre like 'Capacidad %'""");
        bd.update("""
                insert into registros_peso (animal_id, peso_kg, fecha_medicion)
                select a.id, 200 + m * 15 + (a.id % 20), current_date - (m * 60)
                from animales a join fincas f on f.id = a.finca_id cross join generate_series(0, 5) m
                where f.nombre like 'Capacidad %'""");
        bd.update("""
                insert into registros_sanitarios (animal_id, tipo, descripcion, fecha_programada, fecha_realizacion, estado, observaciones)
                select a.id, (array['VACUNA', 'ENFERMEDAD', 'TRATAMIENTO'])[k], 'Evento ' || k, null, current_date - (k * 90), 'REALIZADA', null
                from animales a join fincas f on f.id = a.finca_id cross join generate_series(1, 3) k
                where f.nombre like 'Capacidad %'""");
        bd.update("""
                insert into registros_sanitarios (animal_id, tipo, descripcion, fecha_programada, fecha_realizacion, estado, observaciones)
                select a.id, 'VACUNA', 'Aftosa ciclo I', current_date + (a.id % 20)::int - 5, null, 'PROGRAMADA', null
                from animales a join fincas f on f.id = a.finca_id
                where f.nombre like 'Capacidad %' and a.id % 4 = 0""");
    }

    private static long medir(Supplier<Respuesta> operacion, int estadoEsperado) {
        long peor = 0;
        for (int i = 0; i < REPETICIONES; i++) {
            long inicio = System.nanoTime();
            Respuesta r = operacion.get();
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            assertEquals(estadoEsperado, r.estado(), r.cuerpo());
            peor = Math.max(peor, ms);
        }
        return peor;
    }

    @Test
    @DisplayName("RNF24 y RNF10 · Con 100 fincas más, cada operación responde en menos de 3 segundos")
    void rnf10_rnf24_cienFincasRespondenEnMenosDeTresSegundos() {
        JdbcTemplate bd = new JdbcTemplate(fuenteDatos);
        crearFincas(bd);
        int fincas = bd.queryForObject("select count(*) from fincas", Integer.class);
        int animales = bd.queryForObject("select count(*) from animales", Integer.class);
        int leche = bd.queryForObject("select count(*) from registros_leche", Integer.class);
        int pesos = bd.queryForObject("select count(*) from registros_peso", Integer.class);
        int sanitarios = bd.queryForObject("select count(*) from registros_sanitarios", Integer.class);
        assertTrue(fincas >= FINCAS, "Hay " + fincas + " fincas");

        ClientePrueba c = ClientePrueba.conSesion(puerto, "capacidad050@prueba.example");
        int[] contador = {0};
        Map<String, Long> tiempos = new LinkedHashMap<>();
        tiempos.put("Iniciar sesión", medir(() -> new ClientePrueba(puerto).post("/api/sesion/ingreso",
                "{\"correo\":\"capacidad050@prueba.example\",\"contrasena\":\"Esperanza26\"}"), 200));
        tiempos.put("Listado de animales (HU_002)", medir(() -> c.get("/api/animales"), 200));
        tiempos.put("Filtrar por propósito (HU_002)", medir(() -> c.get("/api/animales?proposito=LECHE"), 200));
        tiempos.put("Página del animal (HU_013)", medir(() -> c.get("/api/animales/CAP-0002"), 200));
        tiempos.put("Historial sanitario (HU_007)", medir(() -> c.get("/api/sanitario/animales/CAP-0002"), 200));
        tiempos.put("Próximas actividades (HU_009)", medir(() -> c.get("/api/sanitario/proximas?dias=30"), 200));
        tiempos.put("Pendientes vencidas (HU_009)", medir(() -> c.get("/api/sanitario/pendientes"), 200));
        tiempos.put("Historial de peso (HU_010)", medir(() -> c.get("/api/animales/CAP-0002/pesos"), 200));
        tiempos.put("Historial reproductivo (HU_017)", medir(() -> c.get("/api/partos/madres/CAP-0002"), 200));
        tiempos.put("Promedio de leche por parto (HU_019)", medir(() -> c.get("/api/leche/CAP-0002/promedios?periodo=PARTO"), 200));
        tiempos.put("Dashboard (HU_012)", medir(() -> c.get("/api/reportes/dashboard"), 200));
        tiempos.put("Reporte de animales (HU_014)", medir(() -> c.post("/api/reportes", "{\"tipo\":\"ANIMALES\"}"), 200));
        tiempos.put("Registrar animal (HU_001)", medir(() -> c.post("/api/animales",
                animal("CAP-N" + (++contador[0]), "MACHO", null, "ENGORDE")), 201));
        tiempos.put("Registrar peso (HU_010)", medir(() -> c.post("/api/animales/CAP-0001/pesos",
                "{\"pesoKg\":300,\"fechaMedicion\":\"" + HOY.minusDays(++contador[0]) + "\"}"), 201));

        StringBuilder tabla = new StringBuilder()
                .append("\n=== RNF10/RNF24 · Datos: ").append(fincas).append(" fincas, ").append(animales).append(" animales, ")
                .append(leche).append(" registros de leche, ").append(pesos).append(" pesos, ").append(sanitarios)
                .append(" registros sanitarios ===\n")
                .append("=== Peor tiempo de ").append(REPETICIONES).append(" repeticiones (límite ").append(LIMITE_MS).append(" ms) ===\n");
        tiempos.forEach((operacion, ms) -> tabla.append(String.format("RNF10 | %-40s | %5d ms%n", operacion, ms)));
        System.out.println(tabla);

        tiempos.forEach((operacion, ms) -> assertTrue(ms < LIMITE_MS, operacion + " tardó " + ms + " ms"));
    }
}
