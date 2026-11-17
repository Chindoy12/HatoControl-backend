package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.TokenRecuperacion;
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
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public Optional<TokenRecuperacion> buscarVigente(String codigo) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public void marcarUsado(TokenRecuperacion token) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
