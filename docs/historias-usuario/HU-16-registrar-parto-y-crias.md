# HU-16: Registrar parto y crías

**Como** administrador de la finca
**Quiero** registrar el parto de una vaca y las crías que nacieron
**Para** llevar el historial reproductivo de cada vaca y tener registradas las crías desde que nacen.

| Campo | Valor |
|---|---|
| Módulo | Reproducción y producción de leche |
| Requerimientos | RF42, RF43, RF44 |
| Reglas de negocio | RN01, RN09, RN11 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Alta |
| Dependencias | HU-01 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v9 y vista lógica v3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-16 |
| Contexto (flujo) | Pesos, partos, crías y leche diaria |
| Funcional | Reproducción y leche (usa IConsultaAnimales) |
| Lógica | Parto (madre 1 — 0..* partos; parto 0..1 — 0..* crías), Animal.admiteProposito(p) |
| Desarrollo | Formulario (tipo parto → FormularioParto) → ControladorReproduccion.registrarParto() → ServicioReproduccion.registrarParto() → IRepositorioAnimales.buscarPorArete(), guardar() · IRepositorioPartos.guardar(parto) |
| DTO | PartoRequest (crias : List<AnimalRequest>) / PartoResponse |
| Excepciones | RecursoNoEncontradoException (madre) · ReglaNegocioException |
| Entidad-relación | partos, animales.parto_id |
| Secuencia | 09 Registrar parto |
| Mockup | P11 Registrar parto y crías, P20 Parto: validaciones (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Flujo completo de un nacimiento.
- N: ✅ No impone pantallas.
- V: ✅ Es el ejemplo de uso que dio el usuario: «nació una cría».
- E: ✅ Datos del parto y de la cría definidos.
- S: ✅ Un parto con sus crías.
- T: ✅ Asociaciones y validaciones comprobables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro de partos y crías

  Escenario: Registrar un parto con su cría
    Dado que existe una hembra registrada
    Cuando el administrador registra un parto con una fecha no posterior a hoy
    Y registra la cría con su código de arete, nombre, sexo y propósito
    Entonces el sistema guarda el parto asociado a la madre
    Y registra la cría como un animal nuevo con fecha de nacimiento igual a la del parto
    Y la asocia a su madre

  Esquema del escenario: Propósito de la cría según el sexo
    Dado que el administrador registra una cría de sexo "<sexo>"
    Cuando elige su propósito
    Entonces el sistema ofrece solo "<opciones>"

    Ejemplos:
      | sexo   | opciones         |
      | macho  | engorde o padrón |
      | hembra | engorde o leche  |

  Escenario: Parto con dos crías
    Dado que en el parto nacieron dos crías
    Cuando el administrador registra ambas
    Entonces el sistema asocia las dos crías al mismo parto y a la misma madre

  Escenario: Parto sin crías
    Dado que la cría murió al nacer
    Cuando el administrador registra el parto sin crías
    Entonces el sistema guarda el parto en el historial de la madre

  Escenario: Madre inválida
    Dado que el animal seleccionado como madre es un macho o no existe
    Cuando el administrador intenta guardar el parto
    Entonces el sistema no guarda el parto
    Y muestra que debe seleccionar una hembra registrada

  Escenario: Fecha de parto futura
    Dado que el administrador ingresa una fecha de parto posterior a hoy
    Cuando intenta guardar
    Entonces el sistema no guarda el parto

  Escenario: Arete de la cría duplicado
    Dado que el arete ingresado para la cría ya pertenece a otro animal de la finca
    Cuando el administrador intenta guardar
    Entonces el sistema no guarda la cría
    Y muestra que el código de arete ya está registrado
```

**Supuestos / preguntas abiertas**
- La cría necesita un código de arete al registrarse (RN01).

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Registrar un parto con su cría | `hu016_c1_partoConCria` | HU_016 C1 y C2 · Parto con una cría; el propósito de la cría depende de su sexo |
| C2 · Propósito de la cría según el sexo | `hu016_c2_propositoCria` | HU_016 C1 y C2 · Parto con una cría; el propósito de la cría depende de su sexo |
| C3 · Parto con dos crías | `hu016_c3_dosCrias` | — |
| C4 · Parto sin crías | `hu016_c4_partoSinCrias` | HU_016 C4 · Parto sin crías |
| C5 · Madre inválida | `hu016_c5_madreInvalida` | — |
| C6 · Fecha de parto futura | `hu016_c6_fechaFutura` | HU_016 C6 · Fecha de parto futura |
| C7 · Arete de la cría duplicado | `hu016_c7_areteCriaDuplicado` | — |
