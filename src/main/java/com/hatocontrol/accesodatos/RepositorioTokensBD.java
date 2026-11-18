package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.TokenRecuperacion;
import java.time.LocalDateTime;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/** Implementa IRepositorioTokens con JPA sobre PostgreSQL. */
@Repository
public class RepositorioTokensBD implements IRepositorioTokens {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(TokenRecuperacion token) {
        if (token.getId() == null) {
            em.persist(token);
        } else {
            em.merge(token);
        }
    }

    @Override
    public Optional<TokenRecuperacion> buscarVigente(String codigo) {
        return em.createQuery("""
                select t from TokenRecuperacion t join fetch t.usuario
                where t.hashToken = :codigo and t.usado = false and t.venceEn > :ahora""", TokenRecuperacion.class)
                .setParameter("codigo", codigo)
                .setParameter("ahora", LocalDateTime.now())
                .getResultStream().findFirst();
    }

    @Override
    public void marcarUsado(TokenRecuperacion token) {
        token.setUsado(true);
        em.merge(token);
    }
}
