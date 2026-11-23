package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.Finca;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFincasBD implements IRepositorioFincas {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(Finca finca) {
        if (finca.getId() == null) {
            em.persist(finca);
        } else {
            em.merge(finca);
        }
    }

    @Override
    public Optional<Finca> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Finca.class, id));
    }
}
