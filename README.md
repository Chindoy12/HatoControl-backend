# HatoControl · Backend

API REST de HatoControl construida con Java 25 y Spring Boot 4.1. El frontend la llama solo a través de `/api`; la base de datos PostgreSQL nunca se expone (RNF11, RNF21).

## Estructura

Los paquetes siguen las capas de la vista de desarrollo (Fase 3).

```
src/main/java/com/hatocontrol/
├── HatoControlAplicacion.java        arranque de Spring Boot
├── presentacion/                     Capa de Presentación
│   ├── Controlador*.java             8 controladores REST
│   ├── FiltroAutenticacion.java      sesión en cada solicitud
│   ├── ControlPermisos.java          permisos por rol
│   ├── ManejadorExcepciones.java     mensajes de error claros
│   ├── ConfiguracionSeguridad.java   rutas públicas, CSRF, codificador de contraseñas
│   └── dto/                          16 DTOs + LecheResponse
├── negocio/                          Capa de Negocio
│   ├── Servicio*.java                8 servicios
│   ├── IServicioCorreo, AdaptadorCorreo
│   ├── IHashContrasenas, HashContrasenasSpring
│   ├── IPeriodoPromedio, PromedioSemanal, PromedioMensual, PromedioPorParto
│   └── excepciones/                  3 excepciones del negocio
├── accesodatos/                      Capa de Acceso a Datos
│   ├── IRepositorio*.java            8 interfaces
│   └── Repositorio*BD.java           8 implementaciones con JPA
└── modelo/                           Modelo: 8 entidades y 5 enumerados
```

Las tablas las crea el repositorio [HatoControl-db](https://github.com/Chindoy12/HatoControl-db) con Flyway; aquí Hibernate solo valida que coincidan (`ddl-auto: validate`).

## API

Todas las rutas empiezan por `/api`. Sin sesión responden 401; las del ganadero, con otro rol, 403. Los errores llegan como `{"mensaje": "...", "campos": {...}}`.

| Método y ruta | Qué hace | HU |
| --- | --- | --- |
| `GET /sesion` · `POST /sesion/ingreso` · `POST /sesion/salida` | sesión actual, ingresar, salir | HU_021 |
| `POST /sesion/recuperacion` · `POST /sesion/recuperacion/{token}` | pedir enlace, crear contraseña nueva | HU_024 |
| `POST /cuentas/fincas` | registrar finca y entrar | HU_020 |
| `GET/POST /cuentas/administradores` · `POST …/desactivacion` · `POST …/contrasena` | administradores (solo ganadero) | HU_023 |
| `GET /animales?filtro&sexo&proposito` · `POST /animales` | listar y registrar | HU_001, HU_002 |
| `GET/PUT/DELETE /animales/{arete}` · `POST /animales/{arete}/engorde` | ficha, editar, eliminar, pasar a engorde | HU_003–005, HU_013, HU_015 |
| `GET/POST /animales/{arete}/pesos` | historial con variaciones y nueva medición | HU_010, HU_011 |
| `POST /sanitario/animales/{arete}/eventos` · `…/programadas` | evento realizado o actividad programada | HU_006, HU_008 |
| `POST /sanitario/eventos/{id}/realizada?fechaRealizacion` | marcar realizada | HU_008 |
| `GET /sanitario/animales/{arete}?tipo` · `GET /sanitario/proximas?dias` · `GET /sanitario/pendientes` | historial y actividades | HU_007, HU_009 |
| `POST /partos` · `GET /partos/madres/{arete}` | parto con crías e historial reproductivo | HU_016, HU_017 |
| `POST /leche` · `GET /leche/{arete}/promedios?periodo&fecha` | leche del día y promedios (SEMANA, MES, PARTO) | HU_018, HU_019 |
| `GET /reportes/dashboard` · `POST /reportes` | dashboard (solo ganadero) y reportes | HU_012, HU_014 |

## Pruebas

Las pruebas usan la API como la aplicación web (cookies de sesión y token CSRF) contra PostgreSQL con los datos de prueba de HatoControl-db. GitHub Actions las ejecuta en cada cambio y lista el resultado de cada una.

| Clase | Pruebas | Qué comprueba |
|---|---|---|
| `CriteriosAceptacionTest` | 80 | Un método por criterio de aceptación que se puede comprobar en la API: `huNNN_cM` = historia NNN, criterio M. |
| `SeguridadTest` | 11 | RNF19 (autenticación y longitud de la contraseña), RNF20 (control de acceso, CSRF y cuenta desactivada con la sesión abierta), RNF22 (hash BCrypt, cookie HttpOnly, nada sensible en respuestas ni registros) y RNF23 (enlace guardado como hash, de un solo uso, vence a los 30 minutos). Usa un correo de prueba que guarda el enlace en lugar de enviarlo. |
| `CapacidadTest` | 1 | RNF24 y RNF10: crea 100 fincas con 40 animales y un año de historial cada una, y comprueba que cada operación responde en menos de 3 segundos. |
| `HatoControlAplicacionTest` | 1 | La aplicación arranca y el esquema de la base coincide con las entidades. |
| `HashContrasenasSpringTest` | 1 | El cifrado de contraseñas con BCrypt. |

Los criterios que solo se ven en pantalla se prueban en el navegador, en `HatoControl-frontend/pruebas`.

## Ejecutar en local

Requiere Java 25, Maven 3.9 y un PostgreSQL con las tablas de HatoControl-db.

```bash
export BD_URL=jdbc:postgresql://localhost:5432/hatocontrol
export BD_USUARIO=hatocontrol
export BD_CONTRASENA=...
mvn spring-boot:run
```

Las credenciales se leen de variables de entorno y nunca se guardan en el repositorio.

## Documentación

- `docs/historias-usuario/`: las 24 historias de usuario.
- `docs/mockups/`: las 25 pantallas (P01–P25) y su relación con las HU.
- `docs/05-vista-desarrollo-v8.png`: vista de desarrollo que coincide con este código.
