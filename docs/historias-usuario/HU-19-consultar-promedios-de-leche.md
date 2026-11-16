# HU-19: Consultar promedios de leche

**Como** ganadero
**Quiero** ver cuánta leche está dando una vaca en promedio por semana, por mes y por parto
**Para** decidir si cumple mis expectativas para seguir en leche.

| Campo | Valor |
|---|---|
| Módulo | Reproducción y producción de leche |
| Requerimientos | RF48, RF38 |
| Reglas de negocio | RN07, RN13 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Media |
| Dependencias | HU-18 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fase 3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-19 |
| Contexto (flujo) | Promedios de leche |
| Funcional | Reproducción y leche |
| Lógica | Parto.promedioDiarioLeche(), RegistroLeche |
| Desarrollo | PaginaAnimal.mostrarPartosYLeche() → ControladorLeche.promedios(periodo) → ServicioLeche.promedio(periodo) → IPeriodoPromedio (PromedioSemanal, PromedioMensual, PromedioPorParto) → IRepositorioLeche.listarEntre(animalId, desde, hasta) |
| DTO | PromedioLecheResponse (registros : List<LecheResponse>) |
| Excepciones | — |
| Entidad-relación | registros_leche |
| Secuencia | — |
| Mockup | Pendiente (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Consulta independiente.
- N: ✅ No impone presentación.
- V: ✅ Es la base para decidir el cambio a engorde (HU-15).
- E: ✅ Fórmula de los promedios definida en RN13.
- S: ✅ Consulta de una vaca.
- T: ✅ Cada promedio se verifica con un cálculo.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Promedios de producción de leche

  Escenario: Promedio de una semana
    Dado que una vaca tiene en una semana registros de 20, 22, 21, 19, 20, 23 y 22 litros
    Cuando el ganadero consulta su producción de esa semana
    Entonces el sistema muestra los siete registros diarios
    Y muestra un promedio de 21 litros por día

  Escenario: Semana con días sin registro
    Dado que una vaca tiene en una semana solo registros de 18, 20 y 22 litros
    Cuando el ganadero consulta esa semana
    Entonces el sistema calcula el promedio solo con los días registrados
    Y muestra 20 litros por día

  Esquema del escenario: Promedios por periodo
    Dado que la vaca tiene registros diarios de leche
    Cuando el ganadero consulta el promedio por "<periodo>"
    Entonces el sistema muestra el total de litros del periodo dividido entre los días con registro

    Ejemplos:
      | periodo |
      | semana  |
      | mes     |
      | parto   |

  Escenario: Periodo sin registros
    Dado que no hay registros de leche en el periodo consultado
    Cuando el ganadero consulta su producción
    Entonces el sistema muestra "sin registros"
    Y no calcula ningún promedio
```

**Supuestos / preguntas abiertas**
- La semana va de lunes a domingo y el mes es el mes calendario (propuesta).
- El promedio usa solo los días con registro; un día sin registro no cuenta como cero (RN13).
