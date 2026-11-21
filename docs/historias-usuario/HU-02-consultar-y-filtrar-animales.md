# HU-02: Consultar y filtrar animales

**Como** ganadero
**Quiero** listar, buscar y filtrar los animales registrados
**Para** encontrar rápido los animales que necesito revisar.

| Campo | Valor |
|---|---|
| Módulo | Gestión de animales |
| Requerimientos | RF06, RF07, RF08 |
| Reglas de negocio | RN01, RN09 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Alta |
| Dependencias | HU-01 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v8)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-02 |
| Contexto (flujo) | Página de cada animal e historiales |
| Funcional | Gestión de animales |
| Lógica | Animal |
| Desarrollo | PaginaAnimales.listar(), filtrar() → ControladorAnimales.listar() → ServicioAnimales.listar(filtro, sexo, proposito) → IRepositorioAnimales.listar(fincaId, filtro) |
| DTO | AnimalResponse |
| Excepciones | — |
| Entidad-relación | animales |
| Secuencia | — |
| Mockup | P06 Listado de animales (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Separa la consulta del registro y la edición.
- N: ✅ No impone el diseño del listado.
- V: ✅ Facilita encontrar animales.
- E: ✅ Columnas, búsqueda y filtros definidos.
- S: ✅ Consulta acotada al listado.
- T: ✅ Resultados verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Consulta del listado de animales

  Escenario: Consultar animales registrados
    Dado que existen animales registrados
    Cuando el ganadero abre el listado
    Entonces el sistema muestra el arete, el nombre, el sexo y el propósito de cada animal

  Escenario: Buscar por arete
    Dado que existe un animal con un código de arete
    Cuando el ganadero busca ese código
    Entonces el sistema muestra el animal correspondiente

  Esquema del escenario: Filtrar el listado
    Dado que existen animales de distinto sexo y propósito
    Cuando el ganadero filtra por "<filtro>"
    Entonces el listado muestra solo los animales que coinciden

    Ejemplos:
      | filtro            |
      | sexo hembra       |
      | sexo macho        |
      | propósito engorde |
      | propósito leche   |
      | propósito padrón  |

  Escenario: Sin coincidencias
    Dado que ningún animal coincide con la búsqueda o el filtro
    Cuando se muestran los resultados
    Entonces el sistema muestra "sin registros"
```

**Supuestos / preguntas abiertas**
- La raza no se registra ni se filtra (decisión de la Fase 2).

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Consultar animales registrados | `hu002_c1_consultarListado` | HU_002 C1 · El listado muestra arete, nombre, sexo y propósito |
| C2 · Buscar por arete | `hu002_c2_buscarPorArete` | HU_002 C2 · Buscar por arete |
| C3 · Filtrar el listado | `hu002_c3_filtrar` | HU_002 C3 · Filtrar por sexo y por propósito |
| C4 · Sin coincidencias | `hu002_c4_sinCoincidencias` | HU_002 C4 · Sin coincidencias se muestra «sin registros» |
