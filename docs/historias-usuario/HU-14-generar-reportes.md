# HU-14: Generar reportes

**Como** ganadero
**Quiero** generar reportes de animales, del historial sanitario y del historial de peso
**Para** revisar la información de la finca respaldada por sus registros.

| Campo | Valor |
|---|---|
| Módulo | Dashboard y reportes |
| Requerimientos | RF35, RF36, RF37, RF38 |
| Reglas de negocio | RN07 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Baja |
| Dependencias | HU-01, HU-06, HU-10 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v9 y vista lógica v3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-14 |
| Contexto (flujo) | Solicitud de dashboard y reportes → Dashboard y reportes |
| Funcional | Dashboard y reportes |
| Lógica | Animal, RegistroSanitario, RegistroPeso |
| Desarrollo | PaginaReportes.generar(tipo) → ControladorReportes.reporte(tipo) → ServicioReportes.reporte(tipo) → IRepositorioAnimales, IRepositorioSanitario, IRepositorioPesos |
| DTO | ReporteRequest (tipo, codigoArete) / ReporteResponse |
| Excepciones | RecursoNoEncontradoException (arete inexistente) |
| Entidad-relación | animales, registros_sanitarios, registros_peso |
| Secuencia | — |
| Mockup | P13 Reportes, P24 Reporte de peso y sin registros (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Reportes del mismo flujo de consulta.
- N: ✅ No impone formato interno ni herramientas.
- V: ✅ Apoya la revisión y la toma de decisiones.
- E: ✅ Tipos de reporte y regla sin datos definidos.
- S: ✅ Tres reportes similares.
- T: ✅ Contenido verificable contra los registros.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Reportes de la finca

  Esquema del escenario: Generar un reporte
    Dado que existen registros para el reporte "<reporte>"
    Cuando el ganadero lo solicita
    Entonces el sistema muestra en pantalla "<contenido>"

    Ejemplos:
      | reporte    | contenido                                           |
      | animales   | el listado de animales con sus datos principales    |
      | sanitario  | los eventos sanitarios guardados del animal elegido |
      | peso       | las mediciones de peso guardadas del animal elegido |

  Escenario: Reporte sin datos
    Dado que no existen registros para el reporte solicitado
    Cuando el ganadero lo genera
    Entonces el sistema muestra "sin registros"
    Y no presenta valores estimados ni inventados
```

**Supuestos / preguntas abiertas**
- Los reportes se muestran en pantalla; la descarga en PDF no está en el alcance (decisión de la Fase 2).
- Para los reportes sanitario y de peso se elige el animal por su código de arete (ReporteRequest.codigoArete).

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Generar un reporte | `hu014_c1_generarReporte` | HU_014 C1 · Reportes de animales, sanitario y peso en pantalla |
| C2 · Reporte sin datos | `hu014_c2_reporteSinDatos` | HU_014 C2 · Reporte sin datos: «sin registros» |
