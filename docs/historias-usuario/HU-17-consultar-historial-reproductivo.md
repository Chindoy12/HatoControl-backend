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

**Trazabilidad con el diseño (Fase 3)**

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
| Mockup | Pendiente (Fase 5) |
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
