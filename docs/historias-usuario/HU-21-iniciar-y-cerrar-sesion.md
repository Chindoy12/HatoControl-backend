# HU-21: Iniciar y cerrar sesión

**Como** usuario registrado
**Quiero** entrar al sistema con mi correo y contraseña y salir cuando termine
**Para** que solo las personas autorizadas vean y modifiquen la información de la finca.

| Campo | Valor |
|---|---|
| Módulo | Fincas, cuentas y acceso |
| Requerimientos | RF51, RF52 |
| Reglas de negocio | RN14, RN16 |
| Usuarios con acceso | Ganadero y administrador |
| Prioridad | Alta |
| Dependencias | HU-20 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fase 3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-21 |
| Contexto (flujo) | Registro de finca y credenciales · Credenciales (administrador) |
| Funcional | Fincas, cuentas y acceso (ofrece API REST e ISesion) |
| Lógica | Usuario.esGanadero(), activo |
| Desarrollo | PaginaIngreso.ingresar() → FiltroAutenticacion (ruta pública) → ControladorAutenticacion.ingresar(), salir() → ServicioAutenticacion.iniciarSesion(), cerrarSesion() → IRepositorioUsuarios.buscarPorCorreo() · IHashContrasenas.verificar() |
| DTO | IngresoRequest / SesionResponse |
| Excepciones | ReglaNegocioException (datos incorrectos) · AccesoDenegadoException (cuenta desactivada) |
| Entidad-relación | usuarios |
| Secuencia | 08 Iniciar sesión |
| Mockup | Pendiente (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Solo requiere que exista una cuenta.
- N: ✅ No impone el mecanismo técnico.
- V: ✅ Protege los datos de la finca.
- E: ✅ Flujo conocido.
- S: ✅ Dos acciones simples.
- T: ✅ Acceso y rechazo verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Acceso al sistema

  Escenario: Iniciar sesión con datos correctos
    Dado que el usuario tiene una cuenta activa de ganadero o de administrador
    Cuando ingresa su correo y su contraseña correctos
    Entonces el sistema le da acceso a las funciones de su tipo de usuario en su finca

  Escenario: Datos incorrectos
    Dado que el usuario ingresa un correo o una contraseña incorrectos
    Cuando intenta entrar
    Entonces el sistema no le da acceso
    Y muestra que el correo o la contraseña no son correctos, sin decir cuál de los dos falló

  Escenario: Cuenta desactivada
    Dado que el ganadero desactivó la cuenta de un administrador
    Cuando ese administrador intenta entrar
    Entonces el sistema no le da acceso
    Y muestra que su cuenta está desactivada

  Escenario: Cerrar sesión
    Dado que el usuario está dentro del sistema
    Cuando cierra la sesión
    Entonces el sistema vuelve a la pantalla de ingreso
    Y no muestra información hasta que inicie sesión de nuevo
```

**Supuestos / preguntas abiertas**
- Ninguno.
