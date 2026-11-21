# HU-12: Consultar dashboard de la finca

**Como** ganadero
**Quiero** ver un resumen de los animales, la sanidad y los últimos pesos
**Para** conocer el estado general de la finca en una sola vista.

| Campo | Valor |
|---|---|
| Módulo | Dashboard y reportes |
| Requerimientos | RF31, RF32, RF33, RF38 |
| Reglas de negocio | RN06, RN07 |
| Usuarios con acceso | Solo el ganadero |
| Prioridad | Media |
| Dependencias | HU-01, HU-06, HU-08, HU-10 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v9 y vista lógica v3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-12 |
| Contexto (flujo) | Solicitud de dashboard y reportes → Dashboard y reportes |
| Funcional | Dashboard y reportes (usa las cuatro IConsulta…) |
| Lógica | Animal, RegistroSanitario.esProxima, estaPendiente, RegistroPeso |
| Desarrollo | PaginaDashboard.mostrarResumen() → ControladorReportes.dashboard() → ServicioReportes.dashboard() → IRepositorioAnimales, IRepositorioSanitario, IRepositorioPesos |
| DTO | ReporteResponse |
| Excepciones | AccesoDenegadoException (administrador) |
| Entidad-relación | animales, registros_sanitarios, registros_peso |
| Secuencia | — |
| Mockup | P12 Dashboard, P25 Avisos y estados vacíos (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Resumen independiente de los reportes detallados.
- N: ✅ No fija el diseño.
- V: ✅ Visión general inmediata.
- E: ✅ Indicadores especificados.
- S: ✅ Se limita al resumen.
- T: ✅ Conteos verificables contra los registros.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Dashboard de la finca

  Escenario: Mostrar el resumen general
    Dado que existen animales y registros en la finca
    Cuando el ganadero consulta el dashboard
    Entonces el sistema muestra el total de animales
    Y muestra la cantidad de vacunas, enfermedades y tratamientos realizados
    Y muestra las actividades próximas y pendientes
    Y muestra el último peso de cada animal que tenga mediciones

  Escenario: Las actividades programadas no se cuentan como realizadas
    Dado que existe una vacuna programada que todavía no se aplica
    Cuando el ganadero consulta el dashboard
    Entonces esa vacuna no se suma a la cantidad de vacunas realizadas
    Y aparece entre las actividades próximas o pendientes

  Escenario: Sección sin datos
    Dado que no hay datos para una sección del dashboard
    Cuando el ganadero consulta el dashboard
    Entonces esa sección muestra "sin registros"
```

**Supuestos / preguntas abiertas**
- El dashboard no incluye todavía indicadores de partos ni de leche; ningún requerimiento los pide.

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Mostrar el resumen general | `hu012_c1_resumenGeneral` | HU_012 C1 · El dashboard muestra el resumen, las actividades y el último peso |
| C2 · Las actividades programadas no se cuentan como realizadas | `hu012_c2_programadasNoSeCuentan` | HU_012 C2 · Una vacuna programada no se cuenta como realizada |
| C3 · Sección sin datos | `hu012_c3_seccionSinDatos` | HU_012 C3 · Secciones sin datos dicen «sin registros» |
