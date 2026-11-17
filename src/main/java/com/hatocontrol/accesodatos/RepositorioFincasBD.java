package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.Finca;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/** Implementa IRepositorioFincas con JPA sobre PostgreSQL. */
@Repository
public class RepositorioFincasBD implements IRepositorioFincas {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(Finca finca) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public Optional<Finca> buscarPorId(Long id) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
