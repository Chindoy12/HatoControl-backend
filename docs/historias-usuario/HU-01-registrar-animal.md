# HU-01: Registrar animal

**Como** administrador de la finca
**Quiero** registrar un animal con su código de arete, sexo y propósito
**Para** tener identificado cada animal y saber para qué se va a utilizar.

| Campo | Valor |
|---|---|
| Módulo | Gestión de animales |
| Requerimientos | RF01, RF02, RF03, RF04, RF05, RF39, RF40 |
| Reglas de negocio | RN01, RN09 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Alta |
| Dependencias | HU-21 (iniciar sesión) |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fase 3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-01 |
| Contexto (flujo) | Datos de animales |
| Funcional | Gestión de animales |
| Lógica | Animal.admiteProposito(p) · enum Sexo, Proposito |
| Desarrollo | Formulario (tipo animal) → ControladorAnimales.registrar() → ServicioAnimales.registrar() → IRepositorioAnimales.buscarPorArete(fincaId, codigo), guardar(animal) |
| DTO | AnimalRequest / AnimalResponse |
| Excepciones | ReglaNegocioException (arete duplicado, fecha futura, propósito no válido) |
| Entidad-relación | animales (UK finca_id + codigo_arete) |
| Secuencia | — |
| Mockup | Pendiente (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Es el flujo inicial de alta de animales.
- N: ✅ No impone pantallas ni tecnología.
- V: ✅ Sin animales registrados no funciona ningún otro módulo.
- E: ✅ Campos, opciones de propósito y validaciones están definidos.
- S: ✅ Se limita al registro de un animal.
- T: ✅ Cada validación tiene un resultado verificable.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro de animales

  Escenario: Registrar animal con datos válidos
    Dado que el administrador está registrando un animal
    Cuando ingresa un código de arete que no está registrado, el sexo, el propósito y una fecha de nacimiento no posterior a hoy
    Entonces el sistema guarda el animal
    Y muestra un mensaje de confirmación

  Esquema del escenario: Propósitos según el sexo
    Dado que el administrador seleccionó el sexo "<sexo>"
    Cuando elige el propósito del animal
    Entonces el sistema ofrece solo las opciones "<opciones>"

    Ejemplos:
      | sexo   | opciones         |
      | macho  | engorde o padrón |
      | hembra | engorde o leche  |

  Escenario: Falta un dato obligatorio
    Dado que el administrador no ingresó el código de arete, el sexo o el propósito
    Cuando solicita guardar el animal
    Entonces el sistema no guarda el animal
    Y señala el campo que falta

  Escenario: Arete duplicado en la misma finca
    Dado que en la finca ya existe un animal con el código de arete ingresado
    Cuando el administrador intenta registrar otro animal con ese código
    Entonces el sistema no guarda el nuevo animal
    Y muestra que el código de arete ya está registrado en la finca

  Escenario: Mismo arete en otra finca
    Dado que otra finca tiene un animal con el código de arete ingresado
    Cuando el administrador registra un animal con ese código en su finca
    Entonces el sistema guarda el animal

  Escenario: Fecha de nacimiento futura
    Dado que el administrador ingresa una fecha de nacimiento posterior a hoy
    Cuando intenta guardar el animal
    Entonces el sistema no guarda el animal
    Y muestra que la fecha de nacimiento no puede ser posterior a hoy

  Escenario: Propósito que no corresponde al sexo
    Dado que el animal es macho
    Cuando se intenta guardar con el propósito "leche"
    Entonces el sistema no guarda el animal
    Y muestra que un macho solo puede ser de engorde o padrón
```

**Supuestos / preguntas abiertas**
- El nombre y la fecha de nacimiento son opcionales; son obligatorios el arete, el sexo y el propósito (RF02).
- Las crías que nacen en la finca se registran desde el parto (HU-16).
