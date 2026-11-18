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
        if (registro.getId() == null) {
            em.persist(registro);
        } else {
            em.merge(registro);
        }
    }

    @Override
    public boolean existeEnFecha(Long animalId, LocalDate fecha) {
        return !em.createQuery("select r.id from RegistroLeche r where r.animal.id = :animal and r.fecha = :fecha", Long.class)
                .setParameter("animal", animalId).setParameter("fecha", fecha)
                .setMaxResults(1).getResultList().isEmpty();
    }

    @Override
    public List<RegistroLeche> listarEntre(Long animalId, LocalDate desde, LocalDate hasta) {
        return em.createQuery("""
                select r from RegistroLeche r where r.animal.id = :animal and r.fecha between :desde and :hasta
                order by r.fecha""", RegistroLeche.class)
                .setParameter("animal", animalId).setParameter("desde", desde).setParameter("hasta", hasta)
                .getResultList();
    }

    @Override
    public boolean existenPara(Long animalId) {
        return !em.createQuery("select r.id from RegistroLeche r where r.animal.id = :animal", Long.class)
                .setParameter("animal", animalId).setMaxResults(1).getResultList().isEmpty();
    }
}
