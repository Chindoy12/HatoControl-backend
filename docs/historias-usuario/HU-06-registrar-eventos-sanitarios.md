# HU-06: Registrar eventos sanitarios

**Como** administrador de la finca
**Quiero** registrar vacunas, enfermedades y tratamientos de un animal
**Para** mantener su historial sanitario al día.

| Campo | Valor |
|---|---|
| Módulo | Control sanitario |
| Requerimientos | RF14, RF15, RF16, RF17 |
| Reglas de negocio | RN02, RN03 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Alta |
| Dependencias | HU-01 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v8)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-06 |
| Contexto (flujo) | Vacunas, enfermedades, tratamientos y actividades |
| Funcional | Control sanitario (usa IConsultaAnimales) |
| Lógica | RegistroSanitario · enum TipoEventoSanitario, EstadoActividad |
| Desarrollo | PaginaSanidad + Formulario (tipo sanitario) → ControladorSanitario.registrarEvento() → ServicioSanitario.registrarEvento() → IRepositorioSanitario.guardar(registro) |
| DTO | EventoSanitarioRequest / EventoSanitarioResponse |
| Excepciones | RecursoNoEncontradoException (animal no seleccionado o inexistente) |
| Entidad-relación | registros_sanitarios |
| Secuencia | — |
| Mockup | P09 Sanidad y actividades, P19 Sanidad: realizar y avisos (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Agrupa registros sanitarios del mismo tipo de flujo.
- N: ✅ No fija una solución técnica.
- V: ✅ Documenta la atención sanitaria (prioridad del 70 % de los encuestados).
- E: ✅ Tipos, fecha, observaciones y asociación definidos.
- S: ✅ Operaciones similares y acotadas.
- T: ✅ Registro y asociación comprobables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro sanitario

  Esquema del escenario: Registrar un evento sanitario
    Dado que existe un animal seleccionado
    Cuando el administrador registra una "<tipo>" con su descripción, fecha y observaciones
    Entonces el sistema guarda el evento asociado al animal como realizado
    Y muestra un mensaje de confirmación

    Ejemplos:
      | tipo        |
      | vacuna      |
      | enfermedad  |
      | tratamiento |

  Escenario: Animal no seleccionado o inexistente
    Dado que no se ha seleccionado un animal existente
    Cuando el administrador intenta guardar un evento sanitario
    Entonces el sistema impide el registro
    Y muestra que debe seleccionar un animal
```

**Supuestos / preguntas abiertas**
- Las actividades futuras (por ejemplo, la próxima vacuna) se registran en HU-08.

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Registrar un evento sanitario | `hu006_c1_registrarEvento` | HU_006 C1 · Registrar una enfermedad realizada muestra la confirmación |
| C2 · Animal no seleccionado o inexistente | `hu006_c2_animalInexistente` | HU_006 C2 · Sin animal seleccionado no se guarda |
