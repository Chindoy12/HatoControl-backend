package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.RegistroPeso;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/** Implementa IRepositorioPesos con JPA sobre PostgreSQL. */
@Repository
public class RepositorioPesosBD implements IRepositorioPesos {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(RegistroPeso registro) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public List<RegistroPeso> historial(Long animalId) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }

    @Override
    public boolean existenPara(Long animalId) {
        throw new UnsupportedOperationException("Se implementa en la Fase 9");
    }
}
