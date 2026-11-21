# HU-20: Registrar finca

**Como** ganadero
**Quiero** registrar mi finca y crear mi cuenta
**Para** administrar la información de mi finca separada de la de otras fincas.

| Campo | Valor |
|---|---|
| Módulo | Fincas, cuentas y acceso |
| Requerimientos | RF49, RF50, RF60 |
| Reglas de negocio | RN15 |
| Usuarios con acceso | Ganadero (antes de tener cuenta) |
| Prioridad | Alta |
| Dependencias | Ninguna |
| Repositorios | HatoControl-frontend, HatoControl-backend, HatoControl-db |

**Trazabilidad con el diseño (Fases 3 y 5; vista de desarrollo v9 y vista lógica v3)**

| Vista | Elementos |
|---|---|
| Casos de uso | CU-20 |
| Contexto (flujo) | Registro de finca y credenciales |
| Funcional | Fincas, cuentas y acceso |
| Lógica | Finca 1 — 1..* Usuario · enum Rol |
| Desarrollo | Formulario (tipo finca → FormularioFinca) con Contador (mostrarContador()) → ControladorCuentas.registrarFinca() → ServicioCuentas.registrarFinca() → IHashContrasenas.cifrar() · IRepositorioUsuarios.buscarPorCorreo(), guardar() · IRepositorioFincas.guardar() |
| DTO | CuentaRequest / SesionResponse (la cuenta queda con la sesión abierta) |
| Excepciones | ReglaNegocioException (correo registrado, contraseña de 10 a 12) |
| Entidad-relación | fincas, usuarios (correo único) |
| Secuencia | — |
| Mockup | P03 Registro de finca, P25 Avisos y estados vacíos (Fase 5) |
| Implementación | Pendiente (Fases 8 a 10) |

**Validación INVEST**
- I: ✅ Es el primer paso de cada finca; no depende de otras historias.
- N: ✅ No impone el formulario.
- V: ✅ Permite que cada finca use el sistema con su propia cuenta.
- E: ✅ Datos y validaciones definidos.
- S: ✅ Un registro.
- T: ✅ Cada validación es verificable.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro de fincas

  Escenario: Registrar una finca nueva
    Dado que un ganadero no tiene cuenta
    Cuando ingresa el nombre de su finca, su nombre, su correo y una contraseña de 10 a 12 caracteres
    Entonces el sistema crea la finca y su cuenta de ganadero
    Y le permite iniciar sesión

  Escenario: Correo ya registrado
    Dado que el correo ingresado ya pertenece a otra cuenta
    Cuando el ganadero intenta registrarse
    Entonces el sistema no crea la finca
    Y muestra que el correo ya está registrado

  Esquema del escenario: Datos incompletos o inválidos
    Dado que el ganadero está registrando su finca
    Cuando "<problema>"
    Entonces el sistema no crea la finca
    Y muestra qué debe corregir

    Ejemplos:
      | problema                                   |
      | falta el nombre de la finca                |
      | falta su nombre o su correo                |
      | la contraseña tiene menos de 10 caracteres |

  Esquema del escenario: Contador de caracteres de la contraseña
    Dado que el ganadero está escribiendo su contraseña
    Cuando lleva "<escritos>" caracteres
    Entonces el sistema muestra "<mensaje>"

    Ejemplos:
      | escritos | mensaje                                   |
      | 6        | Faltan 4 caracteres para el mínimo        |
      | 10       | Puedes escribir 2 caracteres más          |
      | 12       | Llegaste al máximo de 12 caracteres       |

  Escenario: No se pueden escribir más de 12 caracteres
    Dado que la contraseña ya tiene 12 caracteres
    Cuando el ganadero intenta escribir otro
    Entonces el campo no lo acepta
```

**Supuestos / preguntas abiertas**
- El sistema se diseña para al menos 100 fincas, pero no bloquea el registro de más (RNF24).
- La contraseña nunca se guarda ni se devuelve en texto plano (RNF22).

**Implementación y pruebas (Fases 9 a 11)**

Todos los criterios pasan (21/11/2026). `huNNN_cM` está en `CriteriosAceptacionTest` (HatoControl-backend); las pruebas del navegador, en `HatoControl-frontend/pruebas`.

| Criterio | Prueba en la API | Prueba en el navegador |
|---|---|---|
| C1 · Registrar una finca nueva | `hu020_c1_registrarFinca` | HU_020 C1 · Registrar una finca nueva deja al ganadero dentro |
| C2 · Correo ya registrado | `hu020_c2_correoYaRegistrado` | HU_020 C2 · Correo ya registrado |
| C3 · Datos incompletos o inválidos | `hu020_c3_datosInvalidos` | HU_020 C3 · Datos incompletos: se dice qué corregir |
| C4 · Contador de caracteres de la contraseña | — | HU_020 C4 · El contador indica los caracteres que faltan o quedan |
| C5 · No se pueden escribir más de 12 caracteres | — | HU_020 C5 · No se pueden escribir más de 12 caracteres |
