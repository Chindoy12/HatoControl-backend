package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.Parto;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/** Implementa IRepositorioPartos con JPA sobre PostgreSQL. */
@Repository
public class RepositorioPartosBD implements IRepositorioPartos {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(Parto parto) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public List<Parto> listarPorMadre(Long animalId) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public boolean existenPara(Long animalId) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
