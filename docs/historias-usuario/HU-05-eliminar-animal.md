# HU-05: Eliminar animal registrado por error

**Como** administrador de la finca
**Quiero** eliminar un animal que registré por error y que no tiene historial
**Para** depurar el inventario sin perder información de los animales que sí la tienen.

| Campo | Valor |
|---|---|
| Módulo | Gestión de animales |
| Requerimientos | RF12, RF13 |
| Reglas de negocio | RN02, RN05, RN11, RN12 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Media |
| Dependencias | HU-01 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v9 y vista lógica v3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-05 |
| Contexto (flujo) | Datos de animales |
| Funcional | Gestión de animales usa IConsultaSanitaria, IConsultaPesos e IConsultaReproduccion (RF13) |
| Lógica | Animal y sus asociaciones con RegistroSanitario, RegistroPeso, Parto |
| Desarrollo | PaginaAnimal → ControladorAnimales.eliminar() → ServicioAnimales.eliminar() → existenPara(animalId) en IRepositorioSanitario, IRepositorioPesos, IRepositorioPartos, IRepositorioLeche → IRepositorioAnimales.eliminar(animal) |
| DTO | — |
| Excepciones | ReglaNegocioException (tiene historial) · RecursoNoEncontradoException |
| Entidad-relación | animales, registros_sanitarios, registros_peso, partos, registros_leche |
| Secuencia | — |
| Mockup | P08 Página del animal, P18 Eliminar y pasar a engorde (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Operación independiente.
- N: ✅ No prescribe implementación.
- V: ✅ Permite depurar registros creados por error.
- E: ✅ Condiciones de eliminación definidas.
- S: ✅ Se limita a la eliminación protegida.
- T: ✅ Confirmación e historial verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Eliminación de animales

  Escenario: Eliminar animal sin historial
    Dado que el animal no tiene registros sanitarios, de peso, de partos ni de leche
    Y no es madre de ninguna cría registrada
    Cuando el administrador solicita eliminarlo y confirma la acción
    Entonces el sistema elimina el animal

  Escenario: Cancelar la confirmación
    Dado que el administrador inició la eliminación de un animal sin historial
    Cuando cancela la confirmación
    Entonces el sistema conserva el animal

  Escenario: Impedir eliminación con historial
    Dado que el animal tiene registros sanitarios, de peso, de partos o de leche, o es madre de una cría
    Cuando el administrador intenta eliminarlo
    Entonces el sistema impide la eliminación
    Y informa qué registros asociados tiene
```

**Supuestos / preguntas abiertas**
- Ninguno.

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Eliminar animal sin historial | `hu005_c1_eliminarSinHistorial` | HU_005 C1 · Eliminar un animal sin historial pide confirmación y lo elimina |
| C2 · Cancelar la confirmación | — | HU_005 C2 · Cancelar la confirmación conserva el animal |
| C3 · Impedir eliminación con historial | `hu005_c3_impedirConHistorial` | HU_005 C3 · Con historial no se elimina y se dice qué registros tiene |
