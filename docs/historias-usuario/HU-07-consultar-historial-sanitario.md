# HU-07: Consultar historial sanitario

**Como** ganadero
**Quiero** consultar y filtrar el historial sanitario de un animal
**Para** revisar en orden sus vacunas, enfermedades y tratamientos.

| Campo | Valor |
|---|---|
| Módulo | Control sanitario |
| Requerimientos | RF18, RF19, RF38 |
| Reglas de negocio | RN03, RN07 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Media |
| Dependencias | HU-06 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v8)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-07 |
| Contexto (flujo) | Página de cada animal e historiales |
| Funcional | Control sanitario |
| Lógica | RegistroSanitario |
| Desarrollo | PaginaAnimal.mostrarSanidad() → ControladorSanitario.historial() → ServicioSanitario.historial(tipo) → IRepositorioSanitario.historial(animalId) |
| DTO | EventoSanitarioResponse |
| Excepciones | — |
| Entidad-relación | registros_sanitarios |
| Secuencia | — |
| Mockup | P08 Página del animal, P17 Página de un macho (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Consulta independiente del registro.
- N: ✅ Presentación abierta.
- V: ✅ Facilita el seguimiento sanitario.
- E: ✅ Orden y filtros especificados.
- S: ✅ Consulta acotada.
- T: ✅ Orden y tipos comprobables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Historial sanitario

  Escenario: Consultar el historial completo
    Dado que el animal tiene eventos sanitarios realizados
    Cuando el ganadero consulta su historial
    Entonces el sistema muestra las vacunas, enfermedades y tratamientos ordenados por fecha

  Esquema del escenario: Filtrar por tipo de evento
    Dado que el animal tiene eventos sanitarios de varios tipos
    Cuando el ganadero filtra por "<tipo>"
    Entonces el sistema muestra solo los eventos de ese tipo

    Ejemplos:
      | tipo         |
      | vacunas      |
      | enfermedades |
      | tratamientos |

  Escenario: Las actividades programadas no aparecen en el historial
    Dado que el animal tiene una actividad programada que aún no se realiza
    Cuando el ganadero consulta su historial
    Entonces esa actividad no aparece en el historial

  Escenario: Historial sin registros
    Dado que el animal no tiene eventos sanitarios realizados
    Cuando el ganadero consulta o filtra el historial
    Entonces el sistema muestra "sin registros"
```

**Supuestos / preguntas abiertas**
- Una actividad programada aparece en el historial desde que se marca como realizada (decisión de la Fase 2).

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Consultar el historial completo | `hu007_c1_historialOrdenado` | HU_007 C1 y C2 · Historial ordenado y filtrado por tipo |
| C2 · Filtrar por tipo de evento | `hu007_c2_filtrarPorTipo` | HU_007 C1 y C2 · Historial ordenado y filtrado por tipo |
| C3 · Las actividades programadas no aparecen en el historial | `hu007_c3_programadasNoAparecen` | HU_007 C3 y C4 · Las programadas van aparte y un filtro sin datos dice «sin registros» |
| C4 · Historial sin registros | `hu007_c4_historialSinRegistros` | HU_007 C3 y C4 · Las programadas van aparte y un filtro sin datos dice «sin registros» |
