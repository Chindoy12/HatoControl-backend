# HU-18: Registrar producción diaria de leche

**Como** administrador de la finca
**Quiero** registrar cuántos litros de leche dio una vaca cada día
**Para** llevar el control diario de su producción en cada parto.

| Campo | Valor |
|---|---|
| Módulo | Reproducción y producción de leche |
| Requerimientos | RF46, RF47 |
| Reglas de negocio | RN12 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Alta |
| Dependencias | HU-01, HU-16 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v8)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-18 |
| Contexto (flujo) | Pesos, partos, crías y leche diaria |
| Funcional | Reproducción y leche |
| Lógica | RegistroLeche, Animal.esHembraDeLeche() |
| Desarrollo | Formulario (tipo leche) → ControladorLeche.registrar() → ServicioLeche.registrar() → IRepositorioPartos.listarPorMadre(animalId) · IRepositorioLeche.existeEnFecha(animalId, fecha), guardar(registro) |
| DTO | LecheRequest (fechaParto opcional) / LecheResponse |
| Excepciones | ReglaNegocioException (RF47) |
| Entidad-relación | registros_leche (UK animal_id + fecha, parto_id) |
| Secuencia | 10 Registrar leche |
| Mockup | P10 Registrar peso y leche, P21 Leche: validaciones y promedios (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Registro independiente de la consulta.
- N: ✅ No impone el formato de captura.
- V: ✅ Es la forma en que el usuario mide la producción: un registro por día.
- E: ✅ Unidad, periodo y validaciones definidos.
- S: ✅ Un registro diario.
- T: ✅ Cada rechazo tiene una condición verificable.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro diario de leche

  Escenario: Registrar la leche de un día
    Dado que existe una hembra de leche con al menos un parto
    Cuando el administrador ingresa la fecha y los litros producidos ese día
    Entonces el sistema guarda el registro asociado a su parto más reciente
    Y muestra un mensaje de confirmación

  Esquema del escenario: Rechazar un registro inválido
    Dado que el administrador está registrando leche
    Cuando "<problema>"
    Entonces el sistema no guarda el registro
    Y muestra qué debe corregir

    Ejemplos:
      | problema                                       |
      | los litros son cero o negativos                |
      | la fecha es posterior a hoy                    |
      | la fecha es anterior a la fecha del parto      |
      | ese día ya está registrado para la misma vaca  |

  Escenario: Hembra que no es de leche o sin partos
    Dado que la hembra no tiene propósito "leche" o no tiene partos registrados
    Cuando el administrador intenta registrar su leche
    Entonces el sistema no permite el registro
    Y explica el motivo
```

**Supuestos / preguntas abiertas**
- La unidad es el litro.
- El usuario puede elegir otro parto de la vaca (LecheRequest.fechaParto); si no lo elige, se usa el más reciente (RF46).

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Registrar la leche de un día | `hu018_c1_registrarLeche` | HU_018 C1 · Registrar la leche del día en el parto más reciente |
| C2 · Rechazar un registro inválido | `hu018_c2_rechazarRegistroInvalido` | HU_018 C2 · El mismo día no se registra dos veces y no se aceptan litros en cero |
| C3 · Hembra que no es de leche o sin partos | `hu018_c3_hembraSinLecheOSinPartos` | HU_018 C3 · Una hembra de engorde no aparece entre las vacas de leche; HU_018 C3 · Vaca sin partos: se explica el motivo y no se puede guardar |
