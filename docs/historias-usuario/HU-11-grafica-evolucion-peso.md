# HU-11: Ver gráfica de evolución de peso

**Como** ganadero
**Quiero** ver en una gráfica las mediciones de peso de un animal
**Para** reconocer de un vistazo cómo cambia su peso.

| Campo | Valor |
|---|---|
| Módulo | Control de peso |
| Requerimientos | RF30, RF38 |
| Reglas de negocio | RN04, RN07 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Baja |
| Dependencias | HU-10 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v9 y vista lógica v3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-11 |
| Contexto (flujo) | Página de cada animal e historiales |
| Funcional | Control de peso |
| Lógica | RegistroPeso |
| Desarrollo | PaginaAnimal.mostrarPeso() → ControladorPesos.historial() → ServicioPesos.historialConDiferencias() → IRepositorioPesos.historial(animalId) |
| DTO | PesoResponse |
| Excepciones | — |
| Entidad-relación | registros_peso |
| Secuencia | — |
| Mockup | P08 Página del animal, P17 Página de un macho (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Consulta gráfica independiente.
- N: ✅ No prescribe la herramienta de gráficos.
- V: ✅ Facilita interpretar la tendencia.
- E: ✅ Datos a representar definidos.
- S: ✅ Alcance pequeño.
- T: ✅ Puntos y orden verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Gráfica de evolución de peso

  Escenario: Mostrar la evolución
    Dado que el animal tiene registros de peso
    Cuando el ganadero abre la gráfica de evolución
    Entonces el sistema representa cada peso en su fecha de medición

  Escenario: Sin datos para la gráfica
    Dado que el animal no tiene registros de peso
    Cuando el ganadero abre la gráfica
    Entonces el sistema muestra "sin registros"
    Y no inventa ni estima puntos
```

**Supuestos / preguntas abiertas**
- La gráfica muestra solo las mediciones existentes, sin interpolar.

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Mostrar la evolución | — | HU_011 C1 · La gráfica representa cada peso en su fecha |
| C2 · Sin datos para la gráfica | — | HU_013 C3 y HU_011 C2 · Secciones sin datos dicen «sin registros» y no hay gráfica inventada |
