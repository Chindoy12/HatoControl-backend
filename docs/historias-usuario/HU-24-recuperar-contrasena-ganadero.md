# HU-24: Recuperar contraseña del ganadero

**Como** ganadero
**Quiero** recuperar mi contraseña por correo si la olvido
**Para** no perder el acceso a la información de mi finca.

| Campo | Valor |
|---|---|
| Módulo | Fincas, cuentas y acceso |
| Requerimientos | RF59, RF60 |
| Reglas de negocio | --- |
| Usuarios con acceso | Solo el ganadero |
| Prioridad | Media |
| Dependencias | HU-20 |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fase 3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-24 |
| Contexto (flujo) | Enlace de recuperación de contraseña (Servicio de correo) |
| Funcional | Fincas, cuentas y acceso → Servicio de correo |
| Lógica | Usuario |
| Desarrollo | PaginaIngreso.recuperarContrasena() + Formulario (tipo contraseña) → ControladorAutenticacion.solicitarRecuperacion(), restablecerConEnlace() → ServicioAutenticacion.recuperarContrasena() → IRepositorioTokens.guardar(), buscarVigente(), marcarUsado() · IServicioCorreo.enviarEnlace() (AdaptadorCorreo) · IHashContrasenas.cifrar() |
| DTO | IngresoRequest (correo, contrasena nueva) |
| Excepciones | ReglaNegocioException (enlace vencido o usado) |
| Entidad-relación | tokens_recuperacion, usuarios |
| Secuencia | — |
| Mockup | Pendiente (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Flujo independiente.
- N: ✅ No impone el proveedor de correo.
- V: ✅ Evita que el dueño de la finca pierda su cuenta.
- E: ✅ Flujo y condiciones del enlace definidos.
- S: ✅ Un flujo corto.
- T: ✅ Uso y vencimiento del enlace verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Recuperación de contraseña

  Escenario: Solicitar la recuperación
    Dado que el ganadero olvidó su contraseña
    Cuando ingresa el correo de su cuenta en la opción de recuperación
    Entonces el sistema le envía un enlace para crear una contraseña nueva

  Escenario: Crear la contraseña nueva
    Dado que el ganadero abrió el enlace antes de que venza
    Cuando ingresa una contraseña nueva de 10 a 12 caracteres
    Entonces el sistema la guarda
    Y el enlace deja de funcionar

  Escenario: Enlace vencido o ya usado
    Dado que el enlace ya se usó o pasaron más de 30 minutos
    Cuando el ganadero lo abre
    Entonces el sistema no permite cambiar la contraseña
    Y le indica que solicite un enlace nuevo

  Escenario: Correo que no corresponde a un ganadero
    Dado que el correo ingresado no es de una cuenta de ganadero
    Cuando se solicita la recuperación
    Entonces el sistema muestra el mismo mensaje que en una solicitud válida
    Y no envía ningún enlace
```

**Supuestos / preguntas abiertas**
- Los administradores no usan esta opción; su contraseña la restablece el ganadero (HU-23).
- El código del enlace viaja en la dirección del enlace; la contraseña nueva nunca va en la URL (RNF22).
- Los 30 minutos son una propuesta (RNF23).
