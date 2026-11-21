# HU-04: Actualizar animal

**Como** ganadero
**Quiero** corregir los datos de un animal
**Para** mantener su información vigente y sin errores de digitación.

| Campo | Valor |
|---|---|
| Módulo | Gestión de animales |
| Requerimientos | RF03, RF10, RF11, RF40 |
| Reglas de negocio | RN01, RN09 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Media |
| Dependencias | HU-01 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v9 y vista lógica v3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-04 |
| Contexto (flujo) | Datos de animales |
| Funcional | Gestión de animales |
| Lógica | Animal.admiteProposito(p) |
| Desarrollo | Formulario (tipo animal → FormularioAnimal) → ControladorAnimales.actualizar() → ServicioAnimales.actualizar() → IRepositorioAnimales.buscarPorArete(fincaId, codigo), guardar(animal) |
| DTO | AnimalRequest / AnimalResponse |
| Excepciones | ReglaNegocioException (arete duplicado, fecha futura) · RecursoNoEncontradoException |
| Entidad-relación | animales |
| Secuencia | — |
| Mockup | P07 Registrar animal, P16 Editar animal (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Flujo de edición independiente del alta.
- N: ✅ No especifica implementación.
- V: ✅ Evita datos desactualizados o mal digitados.
- E: ✅ Campos editables y validaciones definidos.
- S: ✅ Se limita a modificar un animal existente.
- T: ✅ Cambios y rechazos comprobables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Actualización de animales

  Escenario: Actualizar datos
    Dado que existe un animal registrado
    Cuando el ganadero modifica el nombre o la fecha de nacimiento
    Y la fecha de nacimiento no es posterior a hoy
    Entonces el sistema guarda los cambios
    Y muestra un mensaje de confirmación

  Escenario: Cambiar el arete por uno disponible
    Dado que el ganadero modifica el código de arete
    Y el nuevo código no pertenece a otro animal de la misma finca
    Cuando guarda los cambios
    Entonces el sistema actualiza el arete

  Escenario: Cambiar el arete por uno ya registrado
    Dado que otro animal de la misma finca tiene el nuevo código de arete
    Cuando el ganadero intenta guardar el cambio
    Entonces el sistema rechaza la actualización
    Y conserva el código de arete anterior
    Y muestra que el código ya está registrado

  Escenario: Cambiar el sexo
    Dado que el ganadero cambia el sexo del animal
    Cuando guarda los cambios
    Entonces el sistema le pide elegir de nuevo el propósito entre los que corresponden al nuevo sexo

  Escenario: Fecha de nacimiento futura
    Dado que el ganadero ingresa una fecha de nacimiento posterior a hoy
    Cuando intenta guardar
    Entonces el sistema no guarda los cambios
    Y muestra que la fecha no puede ser posterior a hoy
```

**Supuestos / preguntas abiertas**
- El arete es único dentro de la finca (RN01).
- El cambio de propósito a engorde se trata en HU-15.

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Actualizar datos | `hu004_c1_actualizarDatos` | HU_004 C1 · Actualizar datos muestra la confirmación |
| C2 · Cambiar el arete por uno disponible | `hu004_c2_cambiarAreteDisponible` | — |
| C3 · Cambiar el arete por uno ya registrado | `hu004_c3_cambiarAreteOcupado` | HU_004 C3 · Arete ocupado: se conserva el anterior |
| C4 · Cambiar el sexo | `hu004_c4_cambiarSexo` | HU_004 C4 · Al cambiar el sexo se pide elegir de nuevo el propósito |
| C5 · Fecha de nacimiento futura | `hu004_c5_fechaFutura` | — |
