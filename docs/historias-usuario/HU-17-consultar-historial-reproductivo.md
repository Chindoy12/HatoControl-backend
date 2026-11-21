# HU-17: Consultar historial reproductivo

**Como** ganadero
**Quiero** seleccionar una vaca por su nombre o arete y ver sus partos y sus crías
**Para** conocer cuántas crías ha tenido y en qué fechas.

| Campo | Valor |
|---|---|
| Módulo | Reproducción y producción de leche |
| Requerimientos | RF45, RF38 |
| Reglas de negocio | RN07, RN11 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Media |
| Dependencias | HU-16 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v9 y vista lógica v3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-17 |
| Contexto (flujo) | Página de cada animal e historiales |
| Funcional | Reproducción y leche |
| Lógica | Animal (madre) — Parto — Animal (crías) |
| Desarrollo | PaginaAnimal.mostrarPartosYLeche() → ControladorReproduccion.historialReproductivo() → ServicioReproduccion.historialReproductivo() → IRepositorioPartos.listarPorMadre(animalId) |
| DTO | PartoResponse |
| Excepciones | — |
| Entidad-relación | partos, animales.parto_id |
| Secuencia | — |
| Mockup | P08 Página del animal, P25 Avisos y estados vacíos (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Consulta independiente del registro.
- N: ✅ No impone presentación.
- V: ✅ Es la consulta que pidió el usuario: los hijos y partos de cada vaca.
- E: ✅ Datos a mostrar definidos.
- S: ✅ Consulta de una vaca.
- T: ✅ Orden y crías verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Historial reproductivo

  Escenario: Consultar los partos de una vaca
    Dado que una hembra tiene partos registrados
    Cuando el ganadero la selecciona por su nombre o su código de arete
    Entonces el sistema muestra sus partos ordenados por fecha
    Y muestra las crías de cada parto con su arete, sexo y propósito

  Escenario: Vaca sin partos
    Dado que la hembra no tiene partos registrados
    Cuando el ganadero consulta su historial reproductivo
    Entonces el sistema muestra "sin registros"
```

**Supuestos / preguntas abiertas**
- Solo las hembras tienen historial reproductivo; el padre de la cría no se registra (ningún requerimiento lo pide).

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Consultar los partos de una vaca | `hu017_c1_partosDeUnaVaca` | HU_017 C1 · Historial reproductivo: partos con sus crías |
| C2 · Vaca sin partos | `hu017_c2_vacaSinPartos` | HU_017 C2 · Vaca sin partos: «sin registros» |
