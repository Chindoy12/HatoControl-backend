# HU-15: Pasar un animal a engorde

**Como** ganadero
**Quiero** cambiar a engorde una vaca de leche o un padrón que no cumplió mis expectativas
**Para** darle otro uso sin perder su historial.

| Campo | Valor |
|---|---|
| Módulo | Gestión de animales |
| Requerimientos | RF41 |
| Reglas de negocio | RN10 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Media |
| Dependencias | HU-01 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v8)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-15 |
| Contexto (flujo) | Datos de animales |
| Funcional | Gestión de animales |
| Lógica | Animal.pasarAEngorde() (RN10) |
| Desarrollo | PaginaAnimal → ControladorAnimales.pasarAEngorde() → ServicioAnimales.pasarAEngorde() → IRepositorioAnimales.guardar(animal) |
| DTO | AnimalResponse |
| Excepciones | ReglaNegocioException (ya es de engorde) |
| Entidad-relación | animales |
| Secuencia | — |
| Mockup | P08 Página del animal, P17 Página de un macho, P18 Eliminar y pasar a engorde (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Cambio puntual e independiente.
- N: ✅ No impone cómo se presenta la opción.
- V: ✅ Refleja una decisión real del ganadero sobre su hato.
- E: ✅ Regla de cambio definida en RN10.
- S: ✅ Una sola acción.
- T: ✅ Propósito e historial comprobables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Cambio de propósito a engorde

  Esquema del escenario: Pasar un animal a engorde
    Dado que existe un animal con propósito "<proposito>"
    Cuando el ganadero lo cambia a "engorde" y confirma
    Entonces el sistema guarda el nuevo propósito
    Y el animal conserva todo su historial

    Ejemplos:
      | proposito |
      | leche     |
      | padrón    |

  Escenario: Ya no se registra leche después del cambio
    Dado que una vaca pasó de leche a engorde
    Cuando se intenta registrar su producción de leche
    Entonces el sistema no permite el registro

  Escenario: Cambio no permitido
    Dado que el animal ya tiene propósito "engorde"
    Cuando el ganadero revisa sus opciones
    Entonces el sistema no ofrece ningún cambio de propósito
```

**Supuestos / preguntas abiertas**
- Ninguno.

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Pasar un animal a engorde | `hu015_c1_pasarAEngorde` | HU_015 C1 · Pasar a engorde con confirmación; conserva el historial |
| C2 · Ya no se registra leche después del cambio | `hu015_c2_sinLecheDespuesDelCambio` | — |
| C3 · Cambio no permitido | `hu015_c3_cambioNoPermitido` | HU_015 C3 · Un animal de engorde no ofrece cambio de propósito |
