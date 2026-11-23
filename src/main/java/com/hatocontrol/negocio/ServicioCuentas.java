package com.hatocontrol.negocio;

import com.hatocontrol.accesodatos.IRepositorioFincas;
import com.hatocontrol.accesodatos.IRepositorioUsuarios;
import com.hatocontrol.modelo.Finca;
import com.hatocontrol.modelo.Rol;
import com.hatocontrol.modelo.Usuario;
import com.hatocontrol.negocio.excepciones.RecursoNoEncontradoException;
import com.hatocontrol.negocio.excepciones.ReglaNegocioException;
import com.hatocontrol.presentacion.dto.CuentaRequest;
import com.hatocontrol.presentacion.dto.UsuarioResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioCuentas {

    private final IRepositorioFincas fincas;
    private final IRepositorioUsuarios usuarios;
    private final IHashContrasenas hash;
    private final Clock reloj;

    public ServicioCuentas(IRepositorioFincas fincas, IRepositorioUsuarios usuarios, IHashContrasenas hash, Clock reloj) {
        this.fincas = fincas;
        this.usuarios = usuarios;
        this.hash = hash;
        this.reloj = reloj;
    }

    public Usuario registrarFinca(CuentaRequest solicitud) {
        if (solicitud.nombreFinca() == null || solicitud.nombreFinca().isBlank()) {
            throw new ReglaNegocioException("Escribe el nombre de la finca.");
        }
        correoLibre(solicitud.correo());
        Finca finca = new Finca();
        finca.setNombre(solicitud.nombreFinca().trim());
        finca.setFechaRegistro(LocalDate.now(reloj));
        fincas.guardar(finca);
        Usuario ganadero = nuevoUsuario(finca, solicitud, Rol.GANADERO);
        usuarios.guardar(ganadero);
        return ganadero;
    }

    public UsuarioResponse crearAdministrador(Long fincaId, CuentaRequest solicitud) {
        correoLibre(solicitud.correo());
        Usuario admin = nuevoUsuario(fincas.buscarPorId(fincaId).orElseThrow(), solicitud, Rol.ADMINISTRADOR);
        usuarios.guardar(admin);
        return aRespuesta(admin);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarAdministradores(Long fincaId) {
        return usuarios.listarAdministradores(fincaId).stream().map(ServicioCuentas::aRespuesta).toList();
    }

    public void desactivar(Long fincaId, String correo) {
        Usuario admin = administradorDeLaFinca(fincaId, correo);
        admin.desactivar();
        usuarios.guardar(admin);
    }

    public void restablecerContrasena(Long fincaId, String correo, String contrasenaNueva) {
        validarContrasena(contrasenaNueva);
        Usuario admin = administradorDeLaFinca(fincaId, correo);
        admin.setHashContrasena(hash.cifrar(contrasenaNueva));
        usuarios.guardar(admin);
    }

    // ---- apoyo ----

    static void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 10 || contrasena.length() > 12) {
            throw new ReglaNegocioException("La contraseña debe tener entre 10 y 12 caracteres.");
        }
    }

    private void correoLibre(String correo) {
        if (usuarios.buscarPorCorreo(correo.trim()).isPresent()) {
            throw new ReglaNegocioException("El correo ya está registrado.");
        }
    }

    private Usuario administradorDeLaFinca(Long fincaId, String correo) {
        return usuarios.buscarPorCorreo(correo == null ? "" : correo.trim())
                .filter(u -> u.getFinca().getId().equals(fincaId) && u.getRol() == Rol.ADMINISTRADOR)
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay un administrador con ese correo en la finca."));
    }

    private Usuario nuevoUsuario(Finca finca, CuentaRequest solicitud, Rol rol) {
        validarContrasena(solicitud.contrasena());
        Usuario usuario = new Usuario();
        usuario.setFinca(finca);
        usuario.setNombre(solicitud.nombre().trim());
        usuario.setCorreo(solicitud.correo().trim().toLowerCase());
        usuario.setHashContrasena(hash.cifrar(solicitud.contrasena()));
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuario;
    }

    private static UsuarioResponse aRespuesta(Usuario u) {
        return new UsuarioResponse(u.getNombre(), u.getCorreo(), u.getRol(), u.isActivo());
    }
}
