# HU-08: Programar y completar actividad sanitaria

**Como** administrador de la finca
**Quiero** programar actividades sanitarias futuras y marcarlas como realizadas
**Para** no olvidar las vacunas y tratamientos pendientes.

| Campo | Valor |
|---|---|
| Módulo | Control sanitario |
| Requerimientos | RF20, RF21, RF22 |
| Reglas de negocio | RN06 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Alta |
| Dependencias | HU-01 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fase 3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-08 |
| Contexto (flujo) | Vacunas, enfermedades, tratamientos y actividades |
| Funcional | Control sanitario |
| Lógica | RegistroSanitario.marcarRealizada(fecha) · fechaProgramada y fechaRealizacion [0..1] |
| Desarrollo | Formulario (tipo sanitario) → ControladorSanitario.programar(), marcarRealizada() → ServicioSanitario.programar(), marcarRealizada() → IRepositorioSanitario.guardar(registro) |
| DTO | EventoSanitarioRequest / EventoSanitarioResponse |
| Excepciones | ReglaNegocioException (fecha anterior a hoy) |
| Entidad-relación | registros_sanitarios (dos fechas y estado) |
| Secuencia | — |
| Mockup | Pendiente (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Flujo de planificación y cierre de una actividad.
- N: ✅ No prescribe implementación.
- V: ✅ Ayuda a cumplir las actividades sanitarias.
- E: ✅ Fechas y estados definidos.
- S: ✅ Limitada a programar y completar.
- T: ✅ Fechas y cambio de estado comprobables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Actividades sanitarias

  Escenario: Programar una actividad
    Dado que existe un animal
    Cuando el administrador registra una actividad con una fecha no anterior a hoy
    Entonces el sistema la guarda asociada al animal en estado "programada"

  Escenario: Rechazar fecha pasada
    Dado que el administrador programa una actividad sanitaria
    Cuando ingresa una fecha anterior a hoy
    Entonces el sistema no guarda la actividad
    Y muestra que la fecha no puede ser anterior a hoy

  Escenario: Marcar una actividad como realizada
    Dado que existe una actividad en estado "programada"
    Cuando el administrador la marca como realizada e indica la fecha en que se hizo
    Entonces el sistema cambia su estado a "realizada"
    Y guarda la fecha de realización sin perder la fecha programada
```

**Supuestos / preguntas abiertas**
- La actividad guarda dos fechas: la programada y la de realización (corrige la inconsistencia 5 de la Fase 1).
