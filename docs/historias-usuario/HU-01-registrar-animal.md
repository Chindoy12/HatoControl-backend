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

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v9 y vista lógica v3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-01 |
| Contexto (flujo) | Datos de animales |
| Funcional | Gestión de animales |
| Lógica | Animal.admiteProposito(p) · enum Sexo, Proposito |
| Desarrollo | Formulario (tipo animal → FormularioAnimal) → ControladorAnimales.registrar() → ServicioAnimales.registrar() → IRepositorioAnimales.buscarPorArete(fincaId, codigo), guardar(animal) |
| DTO | AnimalRequest / AnimalResponse |
| Excepciones | ReglaNegocioException (arete duplicado, fecha futura, propósito no válido) |
| Entidad-relación | animales (UK finca_id + codigo_arete) |
| Secuencia | — |
| Mockup | P07 Registrar animal (Fase 5) |
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

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Registrar animal con datos válidos | `hu001_c1_registrarConDatosValidos` | HU_001 C1 · Registrar con datos válidos muestra la confirmación |
| C2 · Propósitos según el sexo | `hu001_c2_propositosSegunSexo` | HU_001 C2 · Solo se ofrecen los propósitos que corresponden al sexo |
| C3 · Falta un dato obligatorio | `hu001_c3_faltaDatoObligatorio` | HU_001 C3 · Si falta un dato obligatorio se señala el campo |
| C4 · Arete duplicado en la misma finca | `hu001_c4_areteDuplicado` | HU_001 C4 · El arete duplicado se informa en pantalla |
| C5 · Mismo arete en otra finca | `hu001_c5_mismoAreteEnOtraFinca` | — |
| C6 · Fecha de nacimiento futura | `hu001_c6_fechaNacimientoFutura` | HU_001 C6 · La fecha de nacimiento no puede ser posterior a hoy |
| C7 · Propósito que no corresponde al sexo | `hu001_c7_propositoNoCorrespondeAlSexo` | — |
