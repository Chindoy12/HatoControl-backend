# HU-13: Ver la página del animal

**Como** ganadero
**Quiero** abrir un animal y ver toda su información en una página propia
**Para** revisar su situación completa de forma organizada, sin cambiar de consulta.

| Campo | Valor |
|---|---|
| Módulo | Dashboard y reportes |
| Requerimientos | RF34, RF38 |
| Reglas de negocio | RN07 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Alta |
| Dependencias | HU-01 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v8)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-13 |
| Contexto (flujo) | Página de cada animal e historiales |
| Funcional | Gestión de animales (consulta los otros módulos) |
| Lógica | Animal y todas sus asociaciones |
| Desarrollo | PaginaAnimales.abrirAnimal() → PaginaAnimal.mostrarDatos(), mostrarSanidad(), mostrarPeso(), mostrarPartosYLeche(), volver() → ControladorAnimales.verPagina() → ServicioAnimales.paginaCompleta() |
| DTO | AnimalResponse, EventoSanitarioResponse, PesoResponse, PartoResponse, PromedioLecheResponse |
| Excepciones | RecursoNoEncontradoException |
| Entidad-relación | animales, registros_sanitarios, registros_peso, partos, registros_leche |
| Secuencia | — |
| Mockup | P08 Página del animal, P17 Página de un macho (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Vista compuesta con valor propio.
- N: ✅ No impone el diseño de la página.
- V: ✅ Es la forma organizada de consultar un animal que pidió el usuario.
- E: ✅ Secciones definidas en RF34.
- S: ✅ Acotada a un animal.
- T: ✅ Secciones, mensajes vacíos y navegación comprobables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Página del animal

  Escenario: Abrir la página de una hembra
    Dado que existe una hembra registrada
    Cuando el ganadero la abre desde el listado
    Entonces el sistema muestra una página propia con sus datos y su edad
    Y muestra su historial sanitario y sus actividades
    Y muestra su historial y su gráfica de peso
    Y muestra sus partos, sus crías y su producción de leche

  Escenario: Abrir la página de un macho
    Dado que existe un macho registrado
    Cuando el ganadero lo abre desde el listado
    Entonces el sistema muestra sus datos, su historial sanitario y su peso
    Y no muestra secciones de partos ni de leche

  Escenario: Secciones sin datos
    Dado que el animal no tiene registros en una sección
    Cuando el ganadero abre su página
    Entonces esa sección muestra "sin registros"
    Y las demás secciones se siguen mostrando

  Escenario: Volver al listado
    Dado que el ganadero está en la página de un animal
    Cuando elige volver
    Entonces el sistema muestra de nuevo el listado de animales
```

**Supuestos / preguntas abiertas**
- La página se abre dentro del sistema, no en otra pestaña del navegador (decisión de la Fase 2).

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Abrir la página de una hembra | — | HU_013 C1 · La página de una hembra muestra datos, sanidad, peso, partos y leche |
| C2 · Abrir la página de un macho | — | HU_013 C2 · La página de un macho no muestra partos ni leche |
| C3 · Secciones sin datos | — | HU_013 C3 y HU_011 C2 · Secciones sin datos dicen «sin registros» y no hay gráfica inventada |
| C4 · Volver al listado | — | HU_013 C4 · Volver al listado |
