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

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v9 y vista lógica v3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-09 |
| Contexto (flujo) | Página de cada animal e historiales |
| Funcional | Control sanitario |
| Lógica | RegistroSanitario.esProxima(hoy, dias), estaPendiente(hoy) |
| Desarrollo | PaginaSanidad.mostrarProximas(dias), mostrarPendientes() · PaginaAnimal.mostrarSanidad() → ControladorSanitario.proximas(), pendientes() → ServicioSanitario.proximas(), pendientes() → IRepositorioSanitario.proximas(fincaId, hoy, dias), pendientes(fincaId, hoy) |
| DTO | EventoSanitarioResponse |
| Excepciones | — |
| Entidad-relación | registros_sanitarios |
| Secuencia | — |
| Mockup | P09 Sanidad y actividades, P19 Sanidad: realizar y avisos (Fase 5) |
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

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Consultar próximas actividades | `hu009_c1_proximas` | HU_009 C1 · Las próximas actividades dependen del número de días |
| C2 · Consultar actividades pendientes vencidas | `hu009_c2_pendientesVencidas` | HU_009 C2 · Las pendientes vencidas se muestran aparte |
| C3 · Sin actividades | `hu009_c3_sinActividades` | HU_009 C3 · Sin actividades se muestra «sin registros» |
