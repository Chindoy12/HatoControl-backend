# HU-09: Consultar actividades sanitarias

**Como** ganadero
**Quiero** consultar las próximas actividades y las pendientes vencidas
**Para** organizar las tareas sanitarias de la finca.

| Campo | Valor |
|---|---|
| Módulo | Control sanitario |
| Requerimientos | RF23, RF24, RF38 |
| Reglas de negocio | RN06, RN07 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Media |
| Dependencias | HU-08 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fase 3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-09 |
| Contexto (flujo) | Página de cada animal e historiales |
| Funcional | Control sanitario |
| Lógica | RegistroSanitario.esProxima(hoy, dias), estaPendiente(hoy) |
| Desarrollo | PaginaAnimal.mostrarSanidad() → ControladorSanitario.proximas(), pendientes() → ServicioSanitario.proximas(), pendientes() → IRepositorioSanitario.proximas(fincaId, hoy, dias), pendientes(fincaId, hoy) |
| DTO | EventoSanitarioResponse |
| Excepciones | — |
| Entidad-relación | registros_sanitarios |
| Secuencia | — |
| Mockup | Pendiente (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Consulta independiente del registro.
- N: ✅ No limita la presentación.
- V: ✅ Ayuda a priorizar tareas.
- E: ✅ Próximas y vencidas están definidas.
- S: ✅ Dos consultas relacionadas.
- T: ✅ Fechas y estado permiten verificar resultados.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Consulta de actividades sanitarias

  Escenario: Consultar próximas actividades
    Dado que existen actividades en estado "programada"
    Cuando el ganadero indica el número de días a consultar
    Entonces el sistema muestra las actividades programadas dentro de ese período

  Escenario: Consultar actividades pendientes vencidas
    Dado que existen actividades programadas con fecha anterior a hoy
    Y todavía no están marcadas como realizadas
    Cuando el ganadero consulta las pendientes
    Entonces el sistema muestra esas actividades

  Escenario: Sin actividades
    Dado que no hay actividades que cumplan el criterio consultado
    Cuando el ganadero revisa las actividades
    Entonces el sistema muestra "sin registros"
```

**Supuestos / preguntas abiertas**
- El número de días lo indica el usuario en cada consulta (RF23).
