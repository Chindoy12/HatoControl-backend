# HU-22: Permisos por usuario y separación de fincas

**Como** ganadero
**Quiero** que cada usuario use solo las funciones de su tipo y vea solo los datos de su finca
**Para** proteger la información de mi finca.

| Campo | Valor |
|---|---|
| Módulo | Fincas, cuentas y acceso |
| Requerimientos | RF53, RF54 |
| Reglas de negocio | RN14, RN15 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Alta |
| Dependencias | HU-21 |
| Repositorios | HatoControl-frontend, HatoControl-backend |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v8)**

| Vista | Elementos |
|---|---|
| Casos de uso | Nota del diagrama de casos de uso (regla transversal) |
| Contexto (flujo) | — |
| Funcional | ISesion, requerida por los cinco módulos |
| Lógica | Finca (cada registro pertenece a una finca) · enum Rol |
| Desarrollo | Encabezado.mostrarMenu(rol) · FiltroAutenticacion.validarSesion(), obtenerFinca() → ServicioAutenticacion.validarCuentaActiva(usuarioId) → IRepositorioUsuarios.buscarPorId(id) · ControlPermisos.verificar(rol, funcion) · todos los IRepositorio… reciben fincaId |
| DTO | SesionResponse (rol) |
| Excepciones | AccesoDenegadoException → ManejadorExcepciones.manejarAccesoDenegado() |
| Entidad-relación | finca_id en usuarios y animales |
| Secuencia | 08 Iniciar sesión |
| Mockup | P02 Ingreso, P05 Panel principal, P22 Vista del administrador, P25 Avisos y estados vacíos (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Reglas transversales verificables por separado.
- N: ✅ No impone cómo se implementan.
- V: ✅ Son las reglas de roles y de fincas que pidió el usuario.
- E: ✅ Matriz de permisos definida (Tabla 2 de requerimientos).
- S: ✅ Dos reglas de acceso relacionadas.
- T: ✅ Cada función y cada finca se pueden probar.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Permisos y separación de fincas

  Esquema del escenario: Funciones reservadas al ganadero
    Dado que un administrador inició sesión
    Cuando busca la opción "<funcion>"
    Entonces el sistema no le muestra esa opción

    Ejemplos:
      | funcion                     |
      | consultar el dashboard      |
      | gestionar administradores   |

  Escenario: Funciones del administrador
    Dado que un administrador inició sesión
    Cuando registra, corrige o elimina un animal, o genera un reporte
    Entonces el sistema realiza la operación

  Escenario: Intento directo de una función no permitida
    Dado que un administrador intenta usar una función reservada al ganadero sin pasar por el menú
    Cuando el sistema recibe la solicitud
    Entonces la rechaza
    Y no cambia ningún dato

  Escenario: Datos de otra finca
    Dado que existen la finca "Los Rosales" y la finca "El Paraíso"
    Cuando un usuario de "Los Rosales" consulta animales, historiales o reportes
    Entonces el sistema muestra solo los datos de "Los Rosales"
```

**Supuestos / preguntas abiertas**
- Ninguno.

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Funciones reservadas al ganadero | — | HU_022 C1 · El administrador no ve Dashboard ni Administradores |
| C2 · Funciones del administrador | `hu022_c2_funcionesDelAdministrador` | — |
| C3 · Intento directo de una función no permitida | `hu022_c3_intentoDirectoRechazado` | HU_022 C3 · Si el administrador escribe la dirección, se le niega y no cambia nada |
| C4 · Datos de otra finca | `hu022_c4_datosDeOtraFinca` | HU_022 C4 · Cada finca ve solo sus animales |
