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
        if (parto.getId() == null) {
            em.persist(parto);
        } else {
            em.merge(parto);
        }
    }

    /** Partos de la madre del más antiguo al más reciente. */
    @Override
    public List<Parto> listarPorMadre(Long animalId) {
        return em.createQuery("select p from Parto p where p.madre.id = :madre order by p.fecha, p.id", Parto.class)
                .setParameter("madre", animalId)
                .getResultList();
    }

    @Override
    public boolean existenPara(Long animalId) {
        return !em.createQuery("select p.id from Parto p where p.madre.id = :madre", Long.class)
                .setParameter("madre", animalId).setMaxResults(1).getResultList().isEmpty();
    }
}
