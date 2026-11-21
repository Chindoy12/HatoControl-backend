# Backlog de historias de usuario · HatoControl

Sistema de Gestión y Control Ganadero. Versión 3 (15 de noviembre de 2026): 24 historias que cubren los 60 requerimientos funcionales (RF01–RF60), alineadas con los diagramas de la Fase 3.

| ID | Historia | Módulo | Prioridad | Usuarios con acceso | Requerimientos |
|---|---|---|---|---|---|
| HU-01 | Registrar animal | Gestión de animales | Alta | Ganadero y administrador | RF01, RF02, RF03, RF04, RF05, RF39, RF40 |
| HU-02 | Consultar y filtrar animales | Gestión de animales | Alta | Ganadero y administrador | RF06, RF07, RF08 |
| HU-03 | Consultar ficha del animal | Gestión de animales | Media | Ganadero y administrador | RF09 |
| HU-04 | Actualizar animal | Gestión de animales | Media | Ganadero y administrador | RF03, RF10, RF11, RF40 |
| HU-05 | Eliminar animal registrado por error | Gestión de animales | Media | Ganadero y administrador | RF12, RF13 |
| HU-06 | Registrar eventos sanitarios | Control sanitario | Alta | Ganadero y administrador | RF14, RF15, RF16, RF17 |
| HU-07 | Consultar historial sanitario | Control sanitario | Media | Ganadero y administrador | RF18, RF19, RF38 |
| HU-08 | Programar y completar actividad sanitaria | Control sanitario | Alta | Ganadero y administrador | RF20, RF21, RF22 |
| HU-09 | Consultar actividades sanitarias | Control sanitario | Media | Ganadero y administrador | RF23, RF24, RF38 |
| HU-10 | Registrar y consultar peso | Control de peso | Alta | Ganadero y administrador | RF25, RF26, RF27, RF28, RF29, RF38 |
| HU-11 | Ver gráfica de evolución de peso | Control de peso | Baja | Ganadero y administrador | RF30, RF38 |
| HU-12 | Consultar dashboard de la finca | Dashboard y reportes | Media | Solo el ganadero | RF31, RF32, RF33, RF38 |
| HU-13 | Ver la página del animal | Dashboard y reportes | Alta | Ganadero y administrador | RF34, RF38 |
| HU-14 | Generar reportes | Dashboard y reportes | Baja | Ganadero y administrador | RF35, RF36, RF37, RF38 |
| HU-15 | Pasar un animal a engorde | Gestión de animales | Media | Ganadero y administrador | RF41 |
| HU-16 | Registrar parto y crías | Reproducción y producción de leche | Alta | Ganadero y administrador | RF42, RF43, RF44 |
| HU-17 | Consultar historial reproductivo | Reproducción y producción de leche | Media | Ganadero y administrador | RF45, RF38 |
| HU-18 | Registrar producción diaria de leche | Reproducción y producción de leche | Alta | Ganadero y administrador | RF46, RF47 |
| HU-19 | Consultar promedios de leche | Reproducción y producción de leche | Media | Ganadero y administrador | RF48, RF38 |
| HU-20 | Registrar finca | Fincas, cuentas y acceso | Alta | Ganadero (antes de tener cuenta) | RF49, RF50, RF60 |
| HU-21 | Iniciar y cerrar sesión | Fincas, cuentas y acceso | Alta | Ganadero y administrador | RF51, RF52 |
| HU-22 | Permisos por usuario y separación de fincas | Fincas, cuentas y acceso | Alta | Ganadero y administrador | RF53, RF54 |
| HU-23 | Gestionar administradores | Fincas, cuentas y acceso | Alta | Solo el ganadero | RF55, RF56, RF57, RF58, RF60 |
| HU-24 | Recuperar contraseña del ganadero | Fincas, cuentas y acceso | Media | Solo el ganadero | RF59, RF60 |

## Usuarios y fincas

- **Ganadero:** registra su finca y es el dueño de esa cuenta; tiene todas las funciones, incluidos el dashboard y la gestión de administradores.
- **Administrador:** su cuenta la crea el ganadero; registra, consulta, corrige y elimina información de los animales y genera reportes. No ve el dashboard ni gestiona cuentas.
- **Fincas:** cada finca tiene su propia cuenta y solo ve sus datos; el sistema se diseña para al menos 100 fincas.

## Regla transversal RF38

Cuando falten datos, el dashboard, la ficha consolidada, los historiales y los reportes muestran **«sin registros»** en lugar de valores vacíos, estimados o inventados.

## Supuestos confirmados en la implementación

1. El enlace para recuperar la contraseña vence a los 30 minutos y sirve una sola vez (RNF23). Prueba: `SeguridadTest.rnf23_*`.
2. Para los promedios de leche, la semana va de lunes a domingo y el mes es el mes calendario (HU-19). Prueba: `hu019_c1` a `hu019_c4`.

## Estado

Los 90 criterios de aceptación de las 24 historias pasan con pruebas automáticas (informe de pruebas de la Fase 11). Cada archivo HU-NN tiene al final la tabla «Implementación y pruebas».
