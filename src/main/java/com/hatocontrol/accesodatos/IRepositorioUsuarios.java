package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.Usuario;
import java.util.List;
import java.util.Optional;

public interface IRepositorioUsuarios {

    void guardar(Usuario usuario);

    Optional<Usuario> buscarPorCorreo(String correo);

    List<Usuario> listarAdministradores(Long fincaId);
}
