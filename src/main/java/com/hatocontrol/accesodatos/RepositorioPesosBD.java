package com.hatocontrol.accesodatos;

import com.hatocontrol.modelo.RegistroPeso;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPesosBD implements IRepositorioPesos {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(RegistroPeso registro) {
        if (registro.getId() == null) {
            em.persist(registro);
        } else {
            em.merge(registro);
        }
    }

    @Override
    public List<RegistroPeso> historial(Long animalId) {
        return em.createQuery("select r from RegistroPeso r where r.animal.id = :animal order by r.fechaMedicion, r.id", RegistroPeso.class)
                .setParameter("animal", animalId)
                .getResultList();
    }

    @Override
    public boolean existenPara(Long animalId) {
        return !em.createQuery("select r.id from RegistroPeso r where r.animal.id = :animal", Long.class)
                .setParameter("animal", animalId).setMaxResults(1).getResultList().isEmpty();
    }
}
