# HU-03: Consultar ficha del animal

**Como** ganadero
**Quiero** consultar la ficha completa de un animal
**Para** revisar sus datos de identificación, su propósito y su edad.

| Campo | Valor |
|---|---|
| Módulo | Gestión de animales |
| Requerimientos | RF09 |
| Reglas de negocio | --- |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Media |
| Dependencias | HU-01 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fase 3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-03 |
| Contexto (flujo) | Página de cada animal e historiales |
| Funcional | Gestión de animales |
| Lógica | Animal.calcularEdadMeses(hoy) |
| Desarrollo | PaginaAnimal.mostrarDatos() → ControladorAnimales.verPagina() → ServicioAnimales.paginaCompleta() → IRepositorioAnimales.buscarPorArete(fincaId, codigo) |
| DTO | AnimalResponse (edadMeses) |
| Excepciones | RecursoNoEncontradoException (animal inexistente) |
| Entidad-relación | animales |
| Secuencia | — |
| Mockup | Pendiente (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Consulta independiente del mantenimiento.
- N: ✅ No prescribe cómo se presenta la ficha.
- V: ✅ Da contexto sobre cada animal.
- E: ✅ Datos y cálculo de edad definidos.
- S: ✅ Consulta acotada.
- T: ✅ Datos y edad comprobables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Ficha del animal

  Escenario: Consultar ficha con fecha de nacimiento
    Dado que existe un animal registrado con fecha de nacimiento
    Cuando el ganadero abre su ficha
    Entonces el sistema muestra el arete, el nombre, el sexo, la fecha de nacimiento y el propósito
    Y muestra su edad calculada en meses

  Escenario: Animal sin fecha de nacimiento
    Dado que el animal no tiene fecha de nacimiento registrada
    Cuando el ganadero abre su ficha
    Entonces el sistema muestra "sin registros" en lugar de la edad

  Escenario: Animal inexistente
    Dado que no existe un animal con el arete consultado
    Cuando el ganadero solicita su ficha
    Entonces el sistema informa que el animal no está registrado
```

**Supuestos / preguntas abiertas**
- La edad se calcula con la fecha actual como referencia.
