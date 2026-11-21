# Infraestructura de HatoControl en AWS (Fase 12)

`hatocontrol.yaml` es una plantilla de CloudFormation. Al subirla una vez en la consola de AWS, crea todo lo que necesita el backend. Después, GitHub Actions publica cada versión del backend sin guardar claves de AWS.

## Qué crea

| Recurso | Para qué | Costo aproximado al mes |
|---|---|---|
| EC2 `t4g.small` con Amazon Linux 2023, Java 25 y Caddy, disco de 20 GB | Corre el backend. Caddy pone el HTTPS con un certificado gratuito de Let's Encrypt en `api.<ip>.sslip.io` | 14 USD (8 USD con `t4g.micro`) |
| IP fija (Elastic IP) | La dirección del backend no cambia | 3,6 USD |
| RDS PostgreSQL 17 `db.t4g.micro`, 20 GB | Base de datos en subredes privadas; solo el backend puede conectarse (RNF21) | 14 USD |
| Secrets Manager (2 secretos) | Contraseña de la base (la genera AWS) y clave de SES | 0,8 USD |
| Amazon SES | Correo de recuperación de contraseña (RF59) | 0 USD con pocos correos |
| S3 privado | El jar que publica GitHub Actions | menos de 0,1 USD |
| Rol de GitHub (OIDC) | Solo la rama `main` de HatoControl-backend puede desplegar | 0 USD |
| Presupuesto | Correo al 80 % y al 100 % del monto, y si el pronóstico lo supera | 0 USD |

En total son unos 32 USD al mes con `t4g.small` y unos 26 USD con `t4g.micro`. En el plan gratuito de AWS se descuentan de los créditos (100 a 200 USD durante 6 meses). El servidor no tiene SSH: se administra con Session Manager desde la consola.

## Pasos, en orden

1. **Cuenta de AWS.** Créala con el plan gratuito y activa la verificación en dos pasos (MFA) del usuario raíz. Arriba a la derecha de la consola, elige la región **N. Virginia (us-east-1)**.
2. **Vercel.** Entra con tu GitHub. En *Add New → Project*, importa `HatoControl-frontend` y despliega (Vercel detecta Vite). Copia la dirección que te da, la nuestra es `https://hato-control-one.vercel.app`. Todavía no funciona el ingreso: falta el backend.
3. **Crear la pila.** En la consola de AWS, entra a *CloudFormation → Create stack → With new resources*.
   - Elige *Upload a template file* y sube `hatocontrol.yaml`.
   - El nombre de la pila debe ser exactamente **`hatocontrol`**.
   - `CrearIdentidadCorreo`: deja `si` la primera vez. Si la pila falla porque el correo remitente «already exists» en SES (queda de un intento anterior), vuelve a crearla con `no`.
   - Llena los parámetros:
     - `CorreoAlertas`: tu correo.
     - `CorreoRemitente`: tu correo o el del proyecto.
     - `UrlFrontend`: la dirección de Vercel.
     - `CargarDatosDePrueba`: `si` para la presentación.
   - Al final, marca *I acknowledge that AWS CloudFormation might create IAM resources* y crea la pila. Tarda unos 15 minutos, por la base de datos.
4. **Verificar el correo.** AWS envía un mensaje a `CorreoRemitente`; abre el enlace. Mientras SES esté en modo de prueba, solo envía a correos verificados. Para la demostración, registra una finca con ese mismo correo y recupera su contraseña.
5. **Conectar GitHub.** En la pestaña *Outputs* de la pila, copia `RolDespliegue`. Luego, en GitHub, ve a *HatoControl-backend → Settings → Secrets and variables → Actions → Variables* y crea la variable `AWS_ROL_DESPLIEGUE` con ese valor. No es una clave secreta: solo dice qué rol usar.
6. **Primer despliegue.** Vuelve a ejecutar el último flujo *Compilar y probar* (o haz un cambio en `main`). El trabajo `desplegar` hace cuatro cosas:
   - sube el jar;
   - migra la base con Flyway;
   - reinicia el backend;
   - comprueba que `https://api.<ip>.sslip.io/api/sesion` responda.
7. **Unir el frontend.** Pon la salida `UrlBackend` en `vercel.json` de HatoControl-frontend (`destination`). Vercel vuelve a publicar solo.

## Si algo falla

- **Registro de instalación del servidor:** abre *EC2 → Instances → hatocontrol-backend → Connect → Session Manager* y ejecuta `sudo cat /var/log/hatocontrol-instalacion.log`.
- **Registro del backend:** `sudo journalctl -u hatocontrol -n 100`.
- **Registro de Caddy (certificado HTTPS):** `sudo journalctl -u caddy -n 50`.

## Al terminar el proyecto

Borra la pila en *CloudFormation → hatocontrol → Delete*. La base deja una instantánea final, por seguridad. Bórrala también en *RDS → Snapshots* si ya no la necesitas, porque cobra almacenamiento.
