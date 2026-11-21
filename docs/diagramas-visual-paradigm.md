# Diagramas de Visual Paradigm frente al código (21/11/2026)

Comparé los 5 diagramas de `Diagramas diseño/DiagramasHato` con el código de los tres repositorios, la vista de desarrollo v9, la vista lógica v3 y las historias de usuario. Así quedó cada uno:

| Diagrama en Visual Paradigm | Estado | Qué hacer |
|---|---|---|
| Vista de Contexto | Coincide con el código | Nada |
| Vista funcional | Coincide con el código | Nada |
| Vista de Despliegue | Coincide (es la v1, sin proveedor) | Nada. Si quieres mostrar la nube, agrega la v2 como diagrama nuevo (`06-vista-despliegue-v2.png`) |
| Vista lógica | Le faltan cosas que ya están en el código: `TokenRecuperacion`, la asociación vaca → leche y la clase abstracta `RegistroDeAnimal` | Ver lista 1 (`04-vista-logica-v3.png`) |
| Vista de desarrollo | Es una versión anterior a las fases 9 a 12 | Ver lista 2 (`05-vista-desarrollo-v9.png`) |

## 1. Vista lógica

- **Clase nueva `TokenRecuperacion`:** atributos `-hashToken : String`, `-venceEn : DateTime`, `-usado : boolean`; método `+isUsado() : boolean`.
- **Asociación `Usuario` 1 → 0..* `TokenRecuperacion`**, etiqueta «solicita» (RNF23, tabla `tokens_recuperacion` del diagrama entidad-relación).
- **Asociación `Animal` «1 vaca» → 0..* `RegistroLeche`**, etiqueta «produce» (columna `registros_leche.animal_id` del diagrama entidad-relación; con ella se impide registrar dos veces el mismo día).
- **En `Usuario`:** agrega `+isActivo() : boolean`.
- **En `Animal`:** `+calcularEdadMeses(hoy : Date)` devuelve `Integer [0..1]`, porque sin fecha de nacimiento no hay edad.
- **Clase abstracta nueva `RegistroDeAnimal`** (nombre en cursiva o con «abstract»):
  - `+fechaDelRegistro() : Date` abstracto (en cursiva);
  - `+esPosteriorA(hoy : Date) : boolean`.
- **Generalización (línea continua con triángulo vacío)** de `RegistroLeche`, `RegistroSanitario` y `RegistroPeso` hacia `RegistroDeAnimal`. Cada una agrega `+fechaDelRegistro() : Date`:
  - `RegistroLeche` devuelve `fecha`;
  - `RegistroPeso` devuelve `fechaMedicion`;
  - `RegistroSanitario` devuelve `fechaRealizacion` y, si no la tiene, `fechaProgramada`.
- Las asociaciones con `Animal` se quedan como están.

## 2. Vista de desarrollo

**Aplicación web (frontend)**

- **Clases nuevas:**
  - `PaginaInicio`: `+irAIngreso()`, `+irARegistro()`.
  - `PaginaPanel`: `+mostrarAccesos(rol)`, `+mostrarPendientes()`.
  - `PaginaSanidad`: `+mostrarProximas(dias)`, `+mostrarPendientes()`, `+marcarRealizada()`.
  - `Encabezado`: `+mostrarMenu(rol)`, `+salir()`.
  - `«apoyo visual» piezas`, sin métodos (Icono, Marca, Mensaje, SinRegistros, Dialogo y formato de fechas).
- **`PaginaIngreso`:** cambia `+mostrarContador()` por `+recuperarContrasena()`.
- **`Formulario`:** queda solo con `-tipo : String` y `+mostrar(tipo)`. Conserva la nota de tipos (animal, sanitario, peso, parto, leche, finca, administrador o contraseña).
- **Paquete nuevo `formularios`**, dentro de la aplicación web:
  - `«hook» useFormulario`: `+validarCampos()`, `+mostrarErrores(mensajes)`, `+enviar()`, `+cambiar(campo, valor)`.
  - `«apoyo visual» campos`: CampoTexto, Clave, SelectorAnimal, Sexos, Resultado, Botones y Contador, que hace `mostrarContador()`.
  - `validaciones`: `+exigir(campo, mensaje)`, `+exigirLongitud()`.
  - `«hook» useAnimales`: `+useAnimales()`.
  - 8 componentes, cada uno con `+guardar(valores)` y `-validar(valores)`: `FormularioAnimal`, `FormularioSanitario`, `FormularioPeso`, `FormularioParto`, `FormularioLeche`, `FormularioFinca`, `FormularioAdministrador` y `FormularioContrasena`.
  - Dependencia `Formulario` → paquete `formularios`, con la etiqueta «mostrar(tipo) elige uno de los 8».
  - Dependencia «use» de los 8 formularios hacia `useFormulario`.

**Capa de presentación**

- **Clases nuevas:**
  - `HatoControlAplicacion`: `+main(args)`.
  - `ConfiguracionSeguridad`: `+cadenaDeSeguridad(http)`, `+codificadorDeContrasenas()`.
- **`ManejadorExcepciones`:** quedan dos métodos, `+manejarError(e : HatoControlException)` (reemplaza a los tres `manejar...` anteriores) y `+manejarValidacion(e)`.
- **`ControladorAutenticacion`:** agrega `+sesionActual()`.
- **DTO:**
  - `AnimalResponse`: agrega `-fechaNacimiento : Date`; `edadMeses` es `Integer`.
  - `EventoSanitarioResponse`: agrega `-id : Long`, `-codigoArete : String`, `-nombreAnimal : String`.
  - `ReporteRequest`: agrega `-codigoArete : String`.
  - `LecheRequest`: agrega `-fechaParto : Date`.
  - `PromedioLecheResponse`: agrega `-registros : List<LecheResponse>`.
  - DTO nuevo `LecheResponse`: `-fecha : Date`, `-litros : double`.

**Capa de negocio**

- **`ServicioAutenticacion`:** agrega `+restablecerConEnlace(token, contrasena)` y `+validarCuentaActiva(usuarioId)`.
- **`ServicioAnimales`:** agrega `+listar(filtro, sexo, proposito)`.
- **`ServicioSanitario`:** agrega `+historial(tipo)`.
- **`ServicioLeche`:** el método es `+promedio(periodo, fecha)`.
- **Clase nueva `HashContrasenasSpring`:** realiza `IHashContrasenas`.
- **`IPeriodoPromedio` pasa a ser la clase abstracta `PeriodoPromedio`:**
  - `+nombre() : String {abstract}`, `+rango(vaca, fecha) {abstract}` y `+promediar(registros)`, con el código común.
  - `PromedioSemanal`, `PromedioMensual` y `PromedioPorParto` cambian la realización (punteada) por generalización (continua). Cada una muestra `+nombre()` y `+rango(vaca, fecha)`.
- **Clase abstracta nueva `HatoControlException`:**
  - `#HatoControlException(mensaje)`, con la nota «sealed: solo sus 3 subclases».
  - `RecursoNoEncontradoException` (404), `ReglaNegocioException` (422) y `AccesoDenegadoException` (403) heredan de ella con generalización.

**Capa de acceso a datos**

- **`IRepositorioUsuarios`:** agrega `+buscarPorId(id)`.
- **`IRepositorioSanitario`:** agrega `+buscarPorId(id)`.

**Modelo**

- Agrega `TokenRecuperacion` y `RegistroDeAnimal` (abstracta), con la generalización de los tres registros.

## 3. Diagramas de secuencia (no están en la carpeta)

El código no sigue en tres puntos los diagramas de secuencia 08, 09 y 10 de la Fase 3. Hay que decidir si se cambia el código o los diagramas:

| Diagrama | Dice el diagrama | Hace el código |
|---|---|---|
| 08 Iniciar sesión | Datos incorrectos: `ReglaNegocioException` y respuesta 401 | `AccesoDenegadoException` y respuesta 403; el mensaje es el mismo |
| 08 Iniciar sesión | Ruta `POST /sesiones` | `POST /api/sesion/ingreso` |
| 09 Registrar parto | Madre inexistente: `RecursoNoEncontradoException` (404); macho o fecha futura: `ReglaNegocioException` (400) | Madre inexistente o macho: `ReglaNegocioException` con «Selecciona una hembra registrada.» (422) |
| 09 y 10 | Errores de regla de negocio responden 400 | Responden 422; 400 queda para campos vacíos o con formato inválido |

El último paso del diagrama 08 («borra la contraseña del formulario») faltaba en el frontend y ya está corregido, con su prueba.
