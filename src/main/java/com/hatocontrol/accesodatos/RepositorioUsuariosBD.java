package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.Rol;
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
        if (usuario.getId() == null) {
            em.persist(usuario);
        } else {
            em.merge(usuario);
        }
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return em.createQuery("select u from Usuario u join fetch u.finca where lower(u.correo) = lower(:correo)", Usuario.class)
                .setParameter("correo", correo)
                .getResultStream().findFirst();
    }

    @Override
    public List<Usuario> listarAdministradores(Long fincaId) {
        return em.createQuery("select u from Usuario u where u.finca.id = :finca and u.rol = :rol order by u.nombre", Usuario.class)
                .setParameter("finca", fincaId)
                .setParameter("rol", Rol.ADMINISTRADOR)
                .getResultList();
    }
}
