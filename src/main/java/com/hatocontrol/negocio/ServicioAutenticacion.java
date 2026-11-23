package com.hatocontrol.negocio;

import com.hatocontrol.accesodatos.IRepositorioTokens;
import com.hatocontrol.accesodatos.IRepositorioUsuarios;
import com.hatocontrol.modelo.TokenRecuperacion;
import com.hatocontrol.modelo.Usuario;
import com.hatocontrol.negocio.excepciones.AccesoDenegadoException;
import com.hatocontrol.negocio.excepciones.ReglaNegocioException;
import com.hatocontrol.presentacion.dto.IngresoRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioAutenticacion {

    static final String DATOS_INCORRECTOS = "El correo o la contraseña no son correctos.";
    static final String CUENTA_DESACTIVADA = "Tu cuenta está desactivada. Habla con el ganadero de tu finca.";
    static final int MINUTOS_ENLACE = 30;

    private static final Logger LOG = LoggerFactory.getLogger(ServicioAutenticacion.class);
    private static final SecureRandom AZAR = new SecureRandom();

    private final IRepositorioUsuarios usuarios;
    private final IRepositorioTokens tokens;
    private final IHashContrasenas hash;
    private final IServicioCorreo correo;
    private final String urlFrontend;

    public ServicioAutenticacion(IRepositorioUsuarios usuarios, IRepositorioTokens tokens, IHashContrasenas hash,
            IServicioCorreo correo, @Value("${hatocontrol.url-frontend}") String urlFrontend) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.hash = hash;
        this.correo = correo;
        this.urlFrontend = urlFrontend;
    }

    @Transactional(readOnly = true)
    public Usuario iniciarSesion(IngresoRequest solicitud) {
        Usuario usuario = usuarios.buscarPorCorreo(solicitud.correo().trim())
                .filter(u -> hash.verificar(solicitud.contrasena(), u.getHashContrasena()))
                .orElseThrow(() -> new AccesoDenegadoException(DATOS_INCORRECTOS));
        if (!usuario.isActivo()) {
            throw new AccesoDenegadoException(CUENTA_DESACTIVADA);
        }
        return usuario;
    }

    @Transactional(readOnly = true)
    public void validarCuentaActiva(Long usuarioId) {
        boolean activa = usuarioId != null && usuarios.buscarPorId(usuarioId).map(Usuario::isActivo).orElse(false);
        if (!activa) {
            throw new AccesoDenegadoException(CUENTA_DESACTIVADA);
        }
    }

    public void cerrarSesion() {
        // Sin estado en el negocio.
    }

    public void recuperarContrasena(String correoIngresado) {
        if (correoIngresado == null || correoIngresado.isBlank()) {
            throw new ReglaNegocioException("Escribe el correo de tu cuenta.");
        }
        usuarios.buscarPorCorreo(correoIngresado.trim())
                .filter(u -> u.esGanadero() && u.isActivo())
                .ifPresent(this::enviarEnlace);
    }

    public void restablecerConEnlace(String token, String contrasenaNueva) {
        TokenRecuperacion registro = tokens.buscarVigente(sha256(token == null ? "" : token))
                .orElseThrow(() -> new ReglaNegocioException("El enlace venció o ya se usó. Solicita un enlace nuevo."));
        ServicioCuentas.validarContrasena(contrasenaNueva);
        Usuario usuario = registro.getUsuario();
        usuario.setHashContrasena(hash.cifrar(contrasenaNueva));
        usuarios.guardar(usuario);
        tokens.marcarUsado(registro);
    }

    private void enviarEnlace(Usuario ganadero) {
        byte[] bytes = new byte[32];
        AZAR.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        TokenRecuperacion registro = new TokenRecuperacion();
        registro.setUsuario(ganadero);
        registro.setHashToken(sha256(token));
        registro.setVenceEn(LocalDateTime.now().plusMinutes(MINUTOS_ENLACE));
        registro.setUsado(false);
        tokens.guardar(registro);
        try {
            correo.enviarEnlace(ganadero.getCorreo(), urlFrontend + "/recuperar/" + token);
        } catch (RuntimeException e) {
            // No se revela al usuario; queda en el registro del servidor.
            LOG.error("No se pudo enviar el correo de recuperación", e);
        }
    }

    static String sha256(String texto) {
        try {
            byte[] resumen = MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(resumen);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
