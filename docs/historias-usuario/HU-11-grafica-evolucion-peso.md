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

**Trazabilidad con el diseño (Fase 3)**

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
| Mockup | Pendiente (Fase 5) |
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
