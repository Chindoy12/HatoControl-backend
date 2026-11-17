package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.Usuario;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/** Implementa IRepositorioUsuarios con JPA sobre PostgreSQL. */
@Repository
public class RepositorioUsuariosBD implements IRepositorioUsuarios {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(Usuario usuario) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public List<Usuario> listarAdministradores(Long fincaId) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
