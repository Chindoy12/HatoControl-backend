package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.RegistroLeche;
import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/** Implementa IRepositorioLeche con JPA sobre PostgreSQL. */
@Repository
public class RepositorioLecheBD implements IRepositorioLeche {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(RegistroLeche registro) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public boolean existeEnFecha(Long animalId, LocalDate fecha) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public List<RegistroLeche> listarEntre(Long animalId, LocalDate desde, LocalDate hasta) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public boolean existenPara(Long animalId) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
