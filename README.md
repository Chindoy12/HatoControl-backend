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
