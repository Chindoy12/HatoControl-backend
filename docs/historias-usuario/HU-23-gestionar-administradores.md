# HU-23: Gestionar administradores

**Como** ganadero
**Quiero** crear, ver y desactivar las cuentas de mis administradores y restablecer su contraseña
**Para** controlar quién trabaja con la información de mi finca.

| Campo | Valor |
|---|---|
| Módulo | Fincas, cuentas y acceso |
| Requerimientos | RF55, RF56, RF57, RF58, RF60 |
| Reglas de negocio | RN14, RN15, RN16 |
| Usuarios con acceso | Solo el ganadero |
| Prioridad | Alta |
| Dependencias | HU-20 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v8)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-23 |
| Contexto (flujo) | Cuentas de administradores |
| Funcional | Fincas, cuentas y acceso |
| Lógica | Usuario.desactivar(), activo · enum Rol |
| Desarrollo | PaginaAdministradores.crear(), listar(), desactivar(), restablecer() + Formulario (tipo administrador o contraseña) → ControladorCuentas → ServicioCuentas.crearAdministrador(), listarAdministradores(), desactivar(), restablecerContrasena() → IRepositorioUsuarios.guardar(), listarAdministradores(fincaId) · IHashContrasenas.cifrar() |
| DTO | CuentaRequest / UsuarioResponse |
| Excepciones | ReglaNegocioException (correo registrado) · AccesoDenegadoException (administrador) |
| Entidad-relación | usuarios (activo) |
| Secuencia | — |
| Mockup | P14 Administradores, P23 Desactivar y restablecer (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Gestión independiente de los módulos de animales.
- N: ✅ No impone pantallas.
- V: ✅ El ganadero controla el acceso a su finca.
- E: ✅ Acciones definidas.
- S: ✅ Cuatro acciones sobre la misma cuenta.
- T: ✅ Estado de la cuenta y acceso verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Gestión de administradores

  Escenario: Crear un administrador
    Dado que el ganadero inició sesión
    Cuando crea un administrador con nombre, correo y una contraseña inicial de 10 a 12 caracteres
    Y el correo no está registrado
    Entonces el sistema crea la cuenta asociada a su finca
    Y el administrador puede iniciar sesión con esos datos

  Escenario: Correo ya registrado
    Dado que el correo ingresado ya pertenece a otra cuenta
    Cuando el ganadero intenta crear el administrador
    Entonces el sistema no crea la cuenta
    Y muestra que el correo ya está registrado

  Escenario: Ver la lista de administradores
    Dado que la finca tiene administradores
    Cuando el ganadero abre la lista
    Entonces el sistema muestra el nombre, el correo y si la cuenta está activa

  Escenario: Desactivar un administrador
    Dado que un administrador tiene la cuenta activa
    Cuando el ganadero la desactiva y confirma
    Entonces el administrador ya no puede iniciar sesión
    Y los registros que hizo se conservan

  Escenario: Restablecer la contraseña de un administrador
    Dado que un administrador olvidó su contraseña
    Cuando el ganadero le asigna una nueva de 10 a 12 caracteres
    Entonces el administrador puede entrar con la nueva contraseña
    Y la anterior deja de funcionar
```

**Supuestos / preguntas abiertas**
- Al desactivar a un administrador que tiene la sesión abierta, esa sesión deja de servir en su siguiente solicitud (observación O-01 de la Fase 11, `ServicioAutenticacion.validarCuentaActiva`).
- Volver a activar una cuenta desactivada no está en los requerimientos.

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Crear un administrador | `hu023_c1_crearAdministrador` | HU_023 C1 y C3 · Crear un administrador y verlo en la lista |
| C2 · Correo ya registrado | `hu023_c2_correoYaRegistrado` | HU_023 C2 · Correo ya registrado |
| C3 · Ver la lista de administradores | `hu023_c3_listaAdministradores` | HU_023 C1 y C3 · Crear un administrador y verlo en la lista |
| C4 · Desactivar un administrador | `hu023_c4_desactivar` | HU_023 C4 · Desactivar pide confirmación |
| C5 · Restablecer la contraseña de un administrador | `hu023_c5_restablecerContrasena` | HU_023 C5 · Restablecer la contraseña con su contador |
