# HU-10: Registrar y consultar peso

**Como** administrador de la finca
**Quiero** registrar el peso de un animal y consultar su historial con las variaciones
**Para** dar seguimiento a su desarrollo.

| Campo | Valor |
|---|---|
| Módulo | Control de peso |
| Requerimientos | RF25, RF26, RF27, RF28, RF29, RF38 |
| Reglas de negocio | RN04, RN05 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Alta |
| Dependencias | HU-01 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fase 3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-10 |
| Contexto (flujo) | Pesos, partos, crías y leche diaria |
| Funcional | Control de peso (ofrece IConsultaPesos e IRegistroPeso) |
| Lógica | RegistroPeso.diferenciaCon(anterior) |
| Desarrollo | Formulario (tipo peso) → ControladorPesos.registrar(), historial() → ServicioPesos.registrar(), historialConDiferencias() → IRepositorioPesos.guardar(registro), historial(animalId) |
| DTO | PesoRequest / PesoResponse (diferenciaKg) |
| Excepciones | ReglaNegocioException (peso ≤ 0, fecha futura) · RecursoNoEncontradoException |
| Entidad-relación | registros_peso |
| Secuencia | — |
| Mockup | Pendiente (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Flujo de medición acotado.
- N: ✅ No impone implementación.
- V: ✅ Seguimiento del peso (prioridad del 50 % de los encuestados).
- E: ✅ Unidad, ingreso manual y reglas definidos.
- S: ✅ Registro e historial relacionados.
- T: ✅ Validaciones, orden y diferencias comprobables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro e historial de peso

  Escenario: Registrar una medición válida
    Dado que existe un animal seleccionado
    Cuando el administrador ingresa un peso mayor que cero en kilogramos y una fecha no posterior a hoy
    Entonces el sistema guarda la medición asociada al animal
    Y muestra un mensaje de confirmación

  Esquema del escenario: Rechazar una medición inválida
    Dado que el administrador está registrando un peso
    Cuando "<problema>"
    Entonces el sistema no guarda la medición
    Y muestra qué dato debe corregir

    Ejemplos:
      | problema                                  |
      | el peso es cero o negativo                |
      | no seleccionó un animal                   |
      | la fecha de medición es posterior a hoy   |

  Escenario: Consultar historial y variaciones
    Dado que el animal tiene varias mediciones
    Cuando se consulta su historial
    Entonces el sistema muestra los pesos ordenados por fecha
    Y muestra la ganancia o pérdida respecto a la medición anterior

  Escenario: Sin mediciones
    Dado que el animal no tiene registros de peso
    Cuando se consulta el historial
    Entonces el sistema muestra "sin registros"
```

**Supuestos / preguntas abiertas**
- En esta versión el peso se ingresa a mano y se expresa en kilogramos.
